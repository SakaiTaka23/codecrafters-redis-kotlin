package global

public data class ParsedCommand(
    var commandCount: Int,
    val mainCommand: String?,
    val mainCommandLen: Int,
    val subArg: String?,
    val subArgLen: Int, )
