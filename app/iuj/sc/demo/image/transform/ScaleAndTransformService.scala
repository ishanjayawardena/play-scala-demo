package iuj.sc.demo.image.transform

import java.io.File
import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import iuj.sc.demo.actor.image.TransformerSupervisor.{TransformAll, TransformCompleted}
import iuj.sc.demo.config.Keys
import iuj.sc.demo.modules.NamedNames
import play.api.{Configuration, Logger}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ScaleAndTransformService  @Inject()(
                                           config: Configuration,
                                           @Named(NamedNames.ImageTransformer) transformer: ActorRef)
  extends TransformService {

  private implicit val timeout: Timeout = config.get[Int](Keys.ImageTransformationTimeout).seconds

  private val logger = Logger(this.getClass)

  def run(files: List[File])(implicit ec: ExecutionContext): Future[TransformCompleted] = {
    logger.info("Received transform request")
    (transformer ? TransformAll(files))
      .map {
        case result: TransformCompleted =>
          logger.info("Transform completed")
          result
      }(ec)
      .recover {
        case error: Throwable =>
          logger.error(s"Transform error. ${error.getMessage}")
          TransformCompleted(s"Error occurred: ${error.getMessage}", List())
      }(ec)
  }
}
