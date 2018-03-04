package iuj.sc.demo.actor.image

import java.io.File
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.assistedinject.Assisted
import iuj.sc.demo.config.Keys
import iuj.sc.demo.image.ImageService
import iuj.sc.demo.image.transform.{ImageBuilder, ImageInfo, Size}
import iuj.sc.demo.modules.NamedNames
import iuj.sc.demo.storage.{StorageParameters, StorageService}
import iuj.sc.demo.actor.image.TransformerSupervisor.{TransformFailure, TransformSuccess}
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}

class TransformerWorker @Inject()(
                                   config: Configuration,
                                   actorSystem: ActorSystem,
                                   imageBuilder: ImageBuilder,
                                   @Named(NamedNames.LocalStorageService) localStorage: StorageService,
                                   @Named(NamedNames.S3StorageService) s3Storage: StorageService,
                                   @Named(NamedNames.ImageMagic) imageService: ImageService,
                                   @Assisted originalFileName: String,
                                   @Assisted supervisor: ActorRef)
  extends Actor {

  import TransformerWorker._

  implicit private val ec: ExecutionContext =
    actorSystem.dispatchers.lookup(Keys.LongRunningTasksDispatcher)

  private val logger = Logger(this.getClass)

  private var results: List[Result] = Nil

  private val remainingTaskCount = new AtomicInteger(0)

  override def receive: Receive = {

    case request: Transform => transform(request)

    case result: Result => handleResult(result)

    case _ =>
      logger.error("Unknown message. Ignored..")
      unhandled(_)
  }

  private def transform(request: Transform) = {
    logger.debug(s"Transforming ${request.file}")
    // move file to tmp
    val originalFileParams = new StorageParameters(request.file.getName, config.get[String](Keys.TmpImagesPath))
    localStorage.move(request.file, originalFileParams)
    // get all smaller files
    val tasksInFuture = createTransformTasks(request.file.getName)
    localStorage.delete(request.file, originalFileParams)
    remainingTaskCount.addAndGet(tasksInFuture.size)
    tasksInFuture.foreach { taskInFuture =>
      taskInFuture
        .map {
          case result: Result => self ! result
        }
        .recover {
          case error: Throwable => handleError(error)
        }
    }
    ackSupervisor(request.file.getName)
  }

  private def ackSupervisor(fileName: String) = {
    supervisor ! Accepted(fileName)
  }

  private def handleResult(result: Result) = {
    remainingTaskCount.decrementAndGet()
    results = result::results
    logger.debug(s"Notified by subtask ${result.fileName}. remaining task count ${remainingTaskCount.get}")
    if (remainingTaskCount.get == 0) {
      logger.info(s"Transformation completed for '${result.fileName}'...")
      supervisor ! TransformSuccess(result.fileName, results)
      remainingTaskCount.set(0)
      results = Nil
    }
  }

  private def createTransformTasks(fileName: String): List[Future[Result]] = {
    imageBuilder
      .buildImageFromInfo(new ImageInfo(fileName))
      .getAllSmallerDimensions
      .map(size => doTransform(fileName, size))
  }

  private def doTransform(fileName: String, size: Size): Future[Result] = {
    imageService.scale(new File(fileName), size)
      .map {
        case file: File =>
          try {
            val uri = s3Storage.store(file, new StorageParameters(file.getName, config.get[String](Keys.S3TransformedImages)))
            Success(fileName, size.toString, uri)
          } catch {
            case e: Throwable =>
              val msg = s"Could not save transformed file '$fileName' in S3. Error: ${e.getMessage}"
              logger.error(msg)
              Failure(fileName, size.toString, msg)
          }
        case _ =>
          val msg = s"Unrecognized response from image service for file '$fileName':$size"
          logger.error(msg)
          Failure(fileName, size.toString, msg)
      }
      .recover {
        case t: Throwable =>
          val msg = s"Transform operation failed. '$fileName':$size. Error: ${t.getMessage}"
          logger.error(msg)
          Failure(fileName, size.toString, msg)
      }
  }

  private def handleError(error: Throwable) = {
    supervisor ! TransformFailure(originalFileName, error.getMessage)
    logger.error(s"Failure in computing sub tasks for '$originalFileName'. Error: ${error.getMessage}")
  }
}

object TransformerWorker {

  case class Transform(file: File)

  case class Accepted(fileName: String)

  case class Success(override val fileName: String, size: String, uri: URI) extends Result(fileName)

  case class Failure(override val fileName: String, size: String, reason: String) extends Result(fileName)

  class Result(val fileName: String)

  trait Factory {
    def apply(fileName: String, supervisor: ActorRef): Actor
  }
}
