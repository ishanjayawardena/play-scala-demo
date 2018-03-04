package controllers

import java.io.File
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import iuj.sc.demo.image.transform.TransformService
import iuj.sc.demo.actor.image.TransformerSupervisor._
import iuj.sc.demo.config.Keys
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.Logger

import scala.concurrent.ExecutionContext

@Singleton
class ImageTransformationController @Inject()(
                                           actorSystem: ActorSystem,
                                           components: ControllerComponents,
                                           transformService: TransformService)
  extends AbstractController(components) {

  private implicit val ec: ExecutionContext =
    actorSystem.dispatchers.lookup(Keys.LongRunningTasksDispatcher)

  private val logger = Logger(this.getClass)

  def transform = Action.async {
    val inputs = List(
      "1-2-100P-80x120.jpg",
      "1-3-222P-60x90.jpg",
      "1-3-102L-90x60.jpg",
      "23-2-300X-70x70.jpg")
      .map(fileName => new File(fileName))

    transformService.run(inputs).mapTo[TransformCompleted]
      .map { response =>
        logger.info(s"Transform service replied '${response.status}'")
        Ok(views.html.site.transform.transform_results(response))
      }(ec)
      .recover {
        case e: Throwable =>
          InternalServerError(s"Failed to complete image transformation. Error:${e.getMessage}")
      }(ec)
  }
}
