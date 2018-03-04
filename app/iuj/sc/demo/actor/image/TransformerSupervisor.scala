package iuj.sc.demo.actor.image

import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.assistedinject.Assisted
import iuj.sc.demo.config.Keys
import iuj.sc.demo.actor.image.TransformerWorker._
import play.api.Logger
import play.api.libs.concurrent.InjectedActorSupport

import scala.concurrent.ExecutionContext
import scala.util.Random

class TransformerSupervisor @Inject()(
                                       workerFactory: Factory,
                                       actorSystem: ActorSystem,
                                       @Assisted imageTransformer: ActorRef)
  extends Actor
    with InjectedActorSupport {

  import TransformerSupervisor._

  implicit private val ec: ExecutionContext =
    actorSystem.dispatchers.lookup(Keys.LongRunningTasksDispatcher)

  private val logger = Logger(this.getClass)

  private var results: List[TransformResult] = Nil

  private val remainingFileCount = new AtomicInteger(0)

  override def receive: Receive = {

    case request: TransformAll =>
      delegateToWorkers(request)

    case ack: Accepted => handleAckFromWorker(ack)

    case result: TransformResult => handleResult(result)

    case _ =>
      logger.warn("Unknown message")
      unhandled(_)
  }

  private def delegateToWorkers(request: TransformAll) = {
    remainingFileCount.addAndGet(request.files.size)
    results = Nil
    request.files.foreach { file =>
      val worker: ActorRef = injectedChild(
        workerFactory(file.getName, self),
        s"worker-${file.getName}-${System.currentTimeMillis}-${Random.nextInt(10001)}",
        props => props.withDispatcher(Keys.LongRunningTasksDispatcher))
      worker ! Transform(file)
      logger.info(s"Submitted scale task image:'${file.getName}'")
    }
    logger.info(s"Submitted ${request.files.size} scale task(s)")
  }

  private def handleAckFromWorker(m: Accepted) = {
    logger.debug(s"Worker ACK '${m.fileName}'")
  }

  private def handleResult(result: TransformResult) = {
    remainingFileCount.decrementAndGet()
    results = result::results
    logger.debug(s"Notified by subtask ${result.fileName}. remaining files count ${remainingFileCount.get}")
    if (remainingFileCount.get == 0) {
      logger.info(s"Transformation completed for '${result.fileName}'...")
      imageTransformer ! TransformCompleted("Completed Successfully", results)
      remainingFileCount.set(0)
      results = Nil
    }
  }
}

object TransformerSupervisor {
  case class TransformAll(files: List[File])

  case class TransformFailure(override val fileName: String, reason: String) extends TransformResult(fileName)

  case class TransformSuccess(override val fileName: String, results: List[Result]) extends TransformResult(fileName)

  class TransformResult(val fileName: String)

  case class TransformCompleted(status: String, results: List[TransformResult])

  trait Factory {
    def apply(imageTransformer: ActorRef): Actor
  }
}
