package resp

public data class Protocol(
    val arguments: MutableList<String>
)

public fun Protocol.commandCount(): Int = arguments.size
public fun Protocol.isOK(): Boolean = arguments[1] == "ok"
