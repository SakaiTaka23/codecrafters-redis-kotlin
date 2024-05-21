package resp

public data class Protocol(
    val arguments: MutableList<String>,
)

public data class Entry(
    val timeStamp: String,
    val content: List<String>,
)

public data class StreamEntry(
    val streamKey: String,
    val entries: List<Entry>,
)

public fun Protocol.commandCount(): Int = arguments.size
public fun Protocol.isOK(): Boolean = arguments[0] == "OK"
public fun Protocol.countBytes(): Int = this.encodeArray().joinToString("").toByteArray().size
