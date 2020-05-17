// author: Piotr Andrzejewski
import java.io.FileInputStream
import java.io.FileOutputStream

interface ImageFormat {
    fun decode(stream: FileInputStream): ImageMatrix
    fun encode(stream: FileOutputStream, image: ImageMatrix)
}