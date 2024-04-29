package commands

import global.RedisCommand
import global.RedisOutput

public class Echo : CommandRoutes {
    override suspend fun run(command: RedisCommand): RedisOutput = RedisOutput(mutableListOf(command.arguments[0]))
}
