// author: Piotr Andrzejewski
@ExperimentalUnsignedTypes
class Pixel(val row: Int, val column: Int, var color: Color)

@ExperimentalUnsignedTypes
class ImageMatrix(val height: Int, val width: Int, val pixels: Array<Array<Pixel>>)