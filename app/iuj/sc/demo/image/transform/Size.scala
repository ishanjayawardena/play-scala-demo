package iuj.sc.demo.image.transform

class Size(val width: Int, val height: Int) {
  def this(width: Int) = this(width, width)

  def compareTo(that: Size): Int = {
    val thisArea = width * height
    val thatArea = that.width * that.height
    thisArea - thatArea
  }

  override def toString = s"${width}x$height"
}