package iuj.sc.demo.modules

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import iuj.sc.demo.image.ImageService
import iuj.sc.demo.image.imagemagic.IMService
import iuj.sc.demo.image.transform.ImageBuilder
import play.api.Logger

class ImageModule extends AbstractModule {
  private val logger = Logger(this.getClass)

  override def configure = {
    bind(classOf[ImageService])
      .annotatedWith(Names.named(NamedNames.ImageMagic))
      .to(classOf[IMService])
      .asEagerSingleton()
    bind(classOf[ImageBuilder]).asEagerSingleton()
    logger.info("Configured Image services...")
  }
}
