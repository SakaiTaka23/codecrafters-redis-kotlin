package resp

private fun contentCount(count: Int): String = "\$$count".addCRCL()

private fun content(content: String): String = content.addCRCL()

private fun commandCount(count: Int): String = "*$count".addCRCL()

private fun nullBulkString(): String = "\$-1".addCRCL()

private fun simpleString(content: String): String = "+$content"

private fun simpleStringContent(content: String): String = " $content"

private fun String.addCRCL(): String = this + "\r\n"

public fun ByteArray.rdbFileSize(): String = "\$${this.size}".addCRCL()

public fun Protocol.encodeArray(): MutableList<String> {
    val result = mutableListOf(commandCount(this.arguments.size))
    this.arguments.forEach {
        result.add(contentCount(it.length))
        result.add(content(it))
    }
    return result
}

public fun Protocol.bulkString(): MutableList<String> {
    if (this.arguments[0] == "-1") {
        return mutableListOf(nullBulkString())
    }
    val result = mutableListOf<String>()
    this.arguments.forEach {
        result.add(contentCount(it.length))
        result.add(content(it))
    }
    return result
}

public fun Protocol.rdbFile(): String = this.arguments[0]

public fun Protocol.simpleString(): String {
    var result = simpleString(this.arguments[0])
    val arguments = this.arguments
    arguments.removeAt(0)

    arguments.forEach {
        result += simpleStringContent(it)
    }

    return result.addCRCL()
}
