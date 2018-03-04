package iuj.sc.demo.image.transform

import java.io.File

import iuj.sc.demo.actor.image.TransformerSupervisor.TransformCompleted

import scala.concurrent.{ExecutionContext, Future}

trait TransformService {
  def run(files: List[File])(implicit ec: ExecutionContext): Future[TransformCompleted]
}
