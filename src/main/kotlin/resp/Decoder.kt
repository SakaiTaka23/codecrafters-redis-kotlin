package resp

public object Decoder {
    private fun simpleString(string: String): String = if (string == "+") {
        "+"
    } else {
        string.removePrefix("+").deleteCRCL()
    }

    private fun arrayCount(string: String): String = if (string == "*") {
        "*"
    } else {
        string.removePrefix("*").deleteCRCL()
    }

    private fun String.deleteCRCL(): String = this.removeSuffix("\r\n")

    public fun read(encoded: String): String {
        val firstLetter = encoded.first()

        return when (firstLetter) {
            '+' -> simpleString(encoded)
            '*' -> arrayCount(encoded)
            '$' -> ""
            else -> encoded.deleteCRCL()
        }
    }
}
