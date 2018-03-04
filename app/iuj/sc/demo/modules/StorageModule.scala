package iuj.sc.demo.modules

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import iuj.sc.demo.storage.StorageService
import iuj.sc.demo.storage.local.LocalStorageService
import iuj.sc.demo.storage.s3.S3StorageService
import play.api.Logger

class StorageModule extends AbstractModule {
  private val logger = Logger(this.getClass)

  override def configure = {
    bind(classOf[StorageService])
      .annotatedWith(Names.named(NamedNames.LocalStorageService))
      .to(classOf[LocalStorageService])
      .asEagerSingleton()

    bind(classOf[StorageService])
      .annotatedWith(Names.named(NamedNames.S3StorageService))
      .to(classOf[S3StorageService])
      .asEagerSingleton()

    logger.info("Configured Storage services...")
  }
}
