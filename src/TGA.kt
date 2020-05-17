// author: Piotr Andrzejewski
import java.io.FileInputStream
import java.io.FileOutputStream

@ExperimentalUnsignedTypes
class TGA : ImageFormat {
    private val header = mutableListOf<Byte>()
    private val footer = mutableListOf<Byte>()

    override fun decode(stream: FileInputStream): ImageMatrix {
        stream.use {
            addNBytesToHeaderOrFooter(it, true, 12)
            val widthBytes = addNBytesToHeaderOrFooter(it, true, 2)
            val heightBytes = addNBytesToHeaderOrFooter(it, true, 2)
            val width = byteArrayToInt(widthBytes.asUByteArray())
            val height = byteArrayToInt(heightBytes.asUByteArray())
            addNBytesToHeaderOrFooter(it, true, 2)
            val pixels: Array<Array<Pixel>> = Array(height) { i ->
                Array(width) { j ->
                    Pixel(i, j, Color(0u, 0u, 0u))
                }
            }

            // due to TGA format, we start from bottom left pixel
            for (i in height - 1 downTo 0) {
                for (j in 0 until width) {
                    pixels[i][j].color = readPixel(it)
                }
            }
            addNBytesToHeaderOrFooter(it, false, 26)
            return ImageMatrix(height, width, pixels)
        }

    }

    /*
        This function is not fully usable - cannot be used before decode()
        Valid use-case:
        1. Decode.
        2. Change some pixels in ImageMatrix.
        3. Encode (uses header and footer saved during decoding).
     */
    override fun encode(stream: FileOutputStream, image: ImageMatrix) {
        stream.use {
            stream.write(header.toByteArray())
            // due to TGA format, we start from bottom left pixel
            for (i in image.height - 1 downTo 0) {
                for (j in 0 until image.width) {
                    writePixel(it, image.pixels[i][j].color)
                }
            }
            stream.write(footer.toByteArray())
        }
    }

    private fun writePixel(stream: FileOutputStream, color: Color) {
        stream.write(color.b.toInt())
        stream.write(color.g.toInt())
        stream.write(color.r.toInt())
    }

    private fun readPixel(stream: FileInputStream): Color {
        // little endian (b, g, r) instead of (r, g, b)
        val blue = stream.read().toUByte()
        val green = stream.read().toUByte()
        val red = stream.read().toUByte()
        return Color(red, green, blue)
    }

    /*
        lovely little endian, we need to read backwards
     */
    private fun byteArrayToInt(byteArray: UByteArray): Int {
        return byteArray
            .reversed() // little endian
            .map { it.toString(2).padStart(8, '0') } // to binary string with padding (full byte)
            .joinToString(separator = "")
            .toInt(2)
    }

    private fun addNBytesToHeaderOrFooter(stream: FileInputStream, toHeader: Boolean, bytesNumber: Int): ByteArray {
        val bytes = stream.readNBytes(bytesNumber)
        if (toHeader) {
            header.addAll(bytes.toList())
        } else {
            footer.addAll(bytes.toList())
        }
        return bytes
    }
}
