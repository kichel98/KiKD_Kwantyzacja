// author: Piotr Andrzejewski
import kotlin.math.abs


@ExperimentalUnsignedTypes
class VectorQuantization {
    fun LBGAlgorithm(image: ImageMatrix, colorsNumber: Byte): ImageMatrix {
        val pixels = flatten(image.pixels)
        val codes = mutableListOf<Color>()
        val quantizationRegions = mutableListOf<MutableSet<Pixel>>() // check if Set removes colors with the same rgb!

        // initialize
        val maxError = 0.01
        codes.add(avgOfColors(pixels))
        quantizationRegions.add(pixels.toMutableSet())
        var previousDistortion = calculateDistortion(codes, quantizationRegions, pixels.size)
        var codebookBits = 0 // counter

        while (true) {
            if (codebookBits < colorsNumber) {
                doubleCodebookSize(codes, quantizationRegions)
                codebookBits++
            }
            updateRegions(codes, quantizationRegions, pixels)
            val actualDistortion = calculateDistortion(codes, quantizationRegions, pixels.size)
            val stopRate = abs((actualDistortion - previousDistortion) / actualDistortion.toDouble())
            if (stopRate < maxError) {
                break
            } else {
                previousDistortion = actualDistortion
            }
            improveCodes(codes, quantizationRegions)
        }
        changeOriginalPixelsToTheirRepresentatives(codes, quantizationRegions, image)
        return image
    }

    private fun changeOriginalPixelsToTheirRepresentatives(codes: MutableList<Color>,
                                                           regions: MutableList<MutableSet<Pixel>>,
                                                           image: ImageMatrix) {
        codes.zip(regions) { code, region ->
            region.forEach { pixel ->
                image.pixels[pixel.row][pixel.column].color = code
            }
        }
    }

    private fun updateRegions(codes: MutableList<Color>, regions: MutableList<MutableSet<Pixel>>, pixels: List<Pixel>) {
        regions.forEach {
            it.clear()
        }
        pixels.forEach { pixel ->
            val bestCode = codes.minBy { code -> taxicabDistance(code, pixel.color) }
            val bestCodeIndex = codes.indexOf(bestCode) // may be improved in future
            regions[bestCodeIndex].add(pixel)
        }
    }

    private fun improveCodes(codes: MutableList<Color>, regions: MutableList<MutableSet<Pixel>>) {
        codes.forEachIndexed { i, _ ->
            if (regions[i].isNotEmpty()) {
                codes[i] = avgOfColors(regions[i])
            }
        }
    }

    private fun doubleCodebookSize(codes: MutableList<Color>, regions: MutableList<MutableSet<Pixel>>) {
        val size = codes.size
        val perturbation = Color((-10..10).random().toUByte(), (-10..10).random().toUByte(), (-10..10).random().toUByte())
        for (i in 0 until size) {
            codes.add(codes[i] + perturbation)
            regions.add(mutableSetOf())
        }
    }

    private fun calculateDistortion(codes: MutableList<Color>,
                                    regions: MutableList<MutableSet<Pixel>>,
                                    pixelSize: Int): Int {
        var sum = 0
        codes.zip(regions) { code, region ->
            region.forEach {
                sum += taxicabDistance(it.color, code)
            }
        }
        return sum / pixelSize
    }

    private fun taxicabDistance(c1: Color, c2: Color): Int {
        return abs((c1.r - c2.r).toInt()) + abs((c1.g - c2.g).toInt()) + abs((c1.b - c2.b).toInt())
    }

    private fun avgOfColors(pixels: Collection<Pixel>): Color {
        var redSum: UInt = 0u
        var greenSum: UInt = 0u
        var blueSum: UInt = 0u
        pixels.forEach {
            redSum += it.color.r
            greenSum += it.color.g
            blueSum += it.color.b
        }
        val count = pixels.size.toUInt()
        // empty set will lead to division by zero!
        return Color((redSum / count).toUByte(), (greenSum / count).toUByte(), (blueSum / count).toUByte())
    }

    private fun flatten(twoDimensionalArray: Array<Array<Pixel>>): List<Pixel> {
        val flattenedList = mutableListOf<Pixel>()
        twoDimensionalArray.forEach {
            flattenedList.addAll(it)
        }
        return flattenedList
    }

}
