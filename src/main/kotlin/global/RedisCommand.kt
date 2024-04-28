package global

public data class RedisCommand(
    val commandCount: Int,
    val commandName: String,
    val arguments: MutableList<String>
)

public fun RedisCommand.toRedisOutput(): RedisOutput = RedisOutput(arguments)

public data class RedisOutput(
    val responses: MutableList<String>
)
