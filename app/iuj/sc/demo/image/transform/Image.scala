package iuj.sc.demo.image.transform

abstract class Image(val imageInfo: ImageInfo, val size: Size, val sizes: List[Size]) {

  def getAllSmallerDimensions: List[Size] = {
    def doGet(list: List[Size], result: List[Size]): List[Size] = {
      list match {
        case Nil => result
        case head::tail if head.compareTo(this.size) <= 0 => doGet(tail, head::result)
        case head::tail if head.compareTo(this.size) > 0 => doGet(tail, result)
      }
    }
    doGet(sizes, Nil)
  }

  override def toString: String = {
    return s"${imageInfo.getDesignerId}-${imageInfo.getDesignId}-${imageInfo.getProductType}${imageInfo.getOrientation}-${size.width}x${size.height}.${imageInfo.getExtension}"
  }
}
