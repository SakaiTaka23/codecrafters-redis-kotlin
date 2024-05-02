package resp

public class Decoder {
    private fun simpleString(string: String): String = string.removePrefix("+").deleteCRCL()
    private fun String.deleteCRCL(): String = this.removeSuffix("\r\n")

    public fun read(encoded: String): String? {
        val firstLetter = encoded.first()

        return when (firstLetter) {
            '+' -> simpleString(encoded)
            '*' -> null
            '$' -> null
            else -> encoded.deleteCRCL()
        }
    }
}
