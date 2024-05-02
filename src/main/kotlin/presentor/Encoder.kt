package presentor


public class Encoder {
    public fun resultContentCount(count: Int): String = "\$$count".addCRCL()

    public fun resultContent(content: String): String = content.addCRCL()

    public fun resultSimpleString(content: String): String = "+$content".addCRCL()

    public fun resultNullBulkString(): String = "\$-1".addCRCL()

    public fun resultCommandCount(count: Int): String = "*$count".addCRCL()
}

private fun String.addCRCL(): String = this + "\r\n"
