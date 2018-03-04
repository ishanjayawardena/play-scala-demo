package iuj.sc.demo.storage.local

import java.io.File
import java.net.URI
import javax.inject.{Inject, Singleton}

import iuj.sc.demo.storage.{StorageParameters, StorageService}
import play.api.Logger

@Singleton
class LocalStorageService extends StorageService {
  private val logger: Logger = Logger(this.getClass)

  // TODO: Implement
  override def store(file: File, parameters: StorageParameters): URI = {
    val uri = getUri(parameters)
    logger.info(s"Stored ${uri}")
    uri
  }

  // TODO: Implement
  override def delete(file:File, parameters: StorageParameters): Unit = {
    logger.info(s"Deleted ${getUri(parameters)}")
  }

  // TODO: Implement
  override def move(file: File, parameters: StorageParameters): Unit = {
    logger.info(s"Moved ${file.toURI} => ${getUri(parameters)}")
  }

  // TODO: Implement
  private def getUri(parameters: StorageParameters): URI = {
    val uri = s"file://${parameters.path}/${parameters.fileName}"
    new URI(uri)
  }

}
