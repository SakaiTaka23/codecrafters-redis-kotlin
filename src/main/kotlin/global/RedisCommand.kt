package global

public data class RedisCommand(
    val commandCount: Int,
    val commandName: String,
    val arguments: MutableList<String>
)

public fun RedisCommand.argumentCount(): Int = arguments.size

public fun RedisCommand.toRedisOutput(): RedisOutput = RedisOutput(arguments)

public data class RedisOutput(
    val responses: MutableList<String>
)

public fun RedisOutput.commandCount(): Int = responses.size
