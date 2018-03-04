package iuj.sc.demo.image

import java.io.File

import iuj.sc.demo.image.transform.Size

import scala.concurrent.{ExecutionContext, Future}

trait ImageService {
  def scale(input: File, size: Size)(implicit ec: ExecutionContext): Future[File]
}
