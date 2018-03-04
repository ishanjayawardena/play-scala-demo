package iuj.sc.demo.storage.s3

import java.io.File
import java.net.URI
import javax.inject.{Inject, Singleton}

import iuj.sc.demo.config.Keys
import iuj.sc.demo.storage.{StorageParameters, StorageService}
import play.api.{Configuration, Logger}

import scala.util.Random

@Singleton
class S3StorageService @Inject()(config: Configuration) extends StorageService {
  private val logger: Logger = Logger(this.getClass)

  // TODO: Implement
  override def store(file: File, parameters: StorageParameters): URI = {
    if (new Random().nextInt(100) % 51 == 0) { // TODO: this is only for testing
      throw new RuntimeException("S3 Invalid secret key..")
    }
    val uri = getUri(parameters)
    logger.info(s"Stored ${uri}")
    uri
  }

  // TODO: Implement
  override def delete(file: File, parameters: StorageParameters): Unit = {
    logger.info(s"Deleted ${getUri(parameters)}")
  }

  // TODO: Implement
  override def move(file: File, parameters: StorageParameters): Unit = {
    logger.info(s"Moved ${getUri(parameters)}")
  }

  // TODO: Implement
  private def getUri(parameters: StorageParameters): URI = {
    val uri = s"http://${config.get[String](Keys.StorageS3HostName)}/${parameters.path}/${parameters.fileName}"
    new URI(uri)
  }
}
