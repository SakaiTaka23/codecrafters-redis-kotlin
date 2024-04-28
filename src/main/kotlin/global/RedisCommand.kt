package global

public data class RedisCommand(
    var commandCount: Int,
    var commandName: String,
    var arguments: MutableList<String>
)
