// author: Piotr Andrzejewski
@ExperimentalUnsignedTypes
data class Color(var r: UByte, var g: UByte, var b: UByte) {
    operator fun plus(color: Color): Color {
        return Color((r + color.r).toUByte(), (g + color.g).toUByte(), (b + color.b).toUByte())
    }
}

