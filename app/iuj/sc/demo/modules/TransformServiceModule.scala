package iuj.sc.demo.modules

import com.google.inject.AbstractModule
import iuj.sc.demo.image.transform.{ScaleAndTransformService, TransformService}
import play.api.Logger

class TransformServiceModule extends AbstractModule {
  private val logger = Logger(this.getClass)

  override def configure = {
    bind(classOf[TransformService])
      .to(classOf[ScaleAndTransformService])
      .asEagerSingleton()
    logger.info("Configured Transform services...")
  }
}
