package iuj.sc.demo.modules

import com.google.inject.AbstractModule
import iuj.sc.demo.actor.image.{ImageTransformer, TransformerSupervisor, TransformerWorker}
import iuj.sc.demo.config.Keys
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.Logger

class ActorsModule extends AbstractModule with AkkaGuiceSupport {
  val logger: Logger = Logger(this.getClass)

  override def configure = {
    // supervisor/worker actor hierarchy for image scaling operation
    bindActor[ImageTransformer](NamedNames.ImageTransformer,
      props => props.withDispatcher(Keys.LongRunningTasksDispatcher))
    bindActorFactory[TransformerSupervisor, TransformerSupervisor.Factory]
    bindActorFactory[TransformerWorker, TransformerWorker.Factory]
    logger.info("supervisor/worker actor hierarchy for image scaling operation..")

    logger.info("Configured Actors...")
  }
}
