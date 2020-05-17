// author: Piotr Andrzejewski
class Pixel(val row: Int, val column: Int, var color: Color)

class ImageMatrix(val height: Int, val width: Int, val pixels: Array<Array<Pixel>>)