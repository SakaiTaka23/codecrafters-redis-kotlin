package commands

import global.RedisCommand
import global.RedisOutput

public class Ping : CommandRoutes {
    override suspend fun run(command: RedisCommand): RedisOutput = RedisOutput(mutableListOf("PONG"))
}
