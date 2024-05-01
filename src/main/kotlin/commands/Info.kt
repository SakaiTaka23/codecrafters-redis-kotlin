package commands

import global.RedisCommand
import global.RedisOutput

public class Info : CommandRoutes {
    override fun run(command: RedisCommand): RedisOutput = when (command.arguments.getOrNull(0)) {
        "replication" -> RedisOutput(mutableListOf("role:master"))
        else -> RedisOutput(mutableListOf())
    }
}
