package iuj.sc.demo.image.imagemagic

import java.io.File
import javax.inject.{Inject, Singleton}

import iuj.sc.demo.image.ImageService
import iuj.sc.demo.image.transform.{ImageBuilder, ImageInfo, Size}
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

@Singleton
class IMService @Inject() (imageBuilder: ImageBuilder) extends ImageService {
  private val logger: Logger = Logger(this.getClass)

  // TODO: Implement
  override def scale(input: File, size: Size)(implicit ec: ExecutionContext): Future[File] = {
    Future {
        try {
          // mimic work
          Thread.sleep(500)
        } catch {
          case e: Throwable => {}
        }
      if (new Random().nextInt(100) % 13 == 0) { // TODO: this is only for testing
        throw new RuntimeException("Cannot connect to the Image Magic Server")
      }
      val scaledImage = imageBuilder.buildImageFromInfo(new ImageInfo(input.getName), size)
      logger.info(s"Scaled image:${input.getName} to size:${size}")
      new File(s"$scaledImage")
    }(ec)
  }
}
