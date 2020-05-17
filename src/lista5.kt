// author: Piotr Andrzejewski
import java.io.File

@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
    if (args.size < 3) {
        println("Usage: lista5.kt image_in.tga image_out.tga colors_number")
        return
    } else {
        val inputPath = args[0]
        val outputPath = args[1]
        val colorsNumber = args[2].toByte()
        val inputStream = File(inputPath).inputStream()
        val outputStream = File(outputPath).outputStream()
        val tga: ImageFormat = TGA()
        var image: ImageMatrix = tga.decode(inputStream)
        val quantization = VectorQuantization()
        image = quantization.LBGAlgorithm(image, colorsNumber)
        tga.encode(outputStream, image)
    }

}