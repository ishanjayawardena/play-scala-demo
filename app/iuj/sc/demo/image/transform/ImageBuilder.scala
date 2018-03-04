package iuj.sc.demo.image.transform

import javax.inject.{Inject, Singleton}

import play.api.Configuration

@Singleton
class ImageBuilder @Inject() (config: Configuration) {

  def buildImageFromInfo(imageInfo: ImageInfo): Image = {
    buildImageFromInfo(imageInfo, new Size(imageInfo.getWidth, imageInfo.getHeight))
  }

  def buildImageFromInfo(imageInfo: ImageInfo, size: Size): Image = {
    imageInfo.getOrientation.toUpperCase match {
      case "L" => new LandscapeImage(imageInfo, size, ImageBuilder.landscapeSizes)
      case "P" => new PortraitImage(imageInfo, size, ImageBuilder.portraitSizes)
      case "X" => new SquareImage(imageInfo, size, ImageBuilder.squareSizes)
      case _ => throw new RuntimeException(s"Unknown image orientation '${imageInfo.getOrientation}'.")
    }
  }
}

object ImageBuilder {
  // TODO: read from input file
  def squareSizes: List[Size] = {
    List(
      new Size(20),
      new Size(30),
      new Size(50),
      new Size(70),
      new Size(100)
    )
  }

  def portraitSizes: List[Size] = {
    List(
      new Size(80, 120),
      new Size(20, 30),
      new Size(40, 60),
      new Size(60, 90),
      new Size(100, 150)
    )
  }

  def landscapeSizes: List[Size] = {
    List(
      new Size(30, 20),
      new Size(60, 40),
      new Size(90, 60),
      new Size(120, 80),
      new Size(150, 100)
    )
  }
}