package iuj.sc.demo.image.transform

class ImageInfo(val fileName: String) {
  private var designerId = ""
  private var designId = ""
  private var productType = ""
  private var orientation = ""
  private var width = 0
  private var height = 0
  private var extension = ""

  fileName match {
    case ImageInfo.IMAGE_FILE_NAME(rDesignerId, rDesignId, rProductType, rOrientation, rWidth, rHeight, rExt) => {
      designerId = rDesignerId
      designId = rDesignId
      productType = rProductType
      orientation = rOrientation
      width = rWidth.toInt
      height = rHeight.toInt
      extension = rExt
    }
    case _ => throw new RuntimeException(s"Unknown image file name format $fileName")
  }

  def getDesignerId = designerId
  def getDesignId = designId
  def getProductType = productType
  def getWidth = width
  def getHeight = height
  def getOrientation = orientation
  def getExtension = extension
}

object ImageInfo {
  private val rDesignerId = raw"(\d{1,})"
  private val rDesignId = raw"(\d{1,})"
  private val rProductType = raw"(\d{1,})"
  private val rOrientation = raw"(\w{1})"
  private val rWidth = raw"(\d{1,})"
  private val rHeight = raw"(\d{1,})"
  private val x = raw"x"
  private val ext = raw"(\w{1,})"
  val IMAGE_FILE_NAME = raw"$rDesignerId-$rDesignId-$rProductType$rOrientation-$rWidth$x$rHeight.$ext".r
}