package commands

import config.Server
import global.RedisCommand
import global.RedisOutput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

public class Info : CommandRoutes, KoinComponent {
    private val server: Server by inject()

    override fun run(command: RedisCommand): RedisOutput = when (command.arguments.getOrNull(0)) {
        "replication" -> {
            val result = mutableListOf<String>()
            if (server.isSlave) {
                result.add("role:slave")
            } else {
                result.add("role:master")
            }
            RedisOutput(result)
        }

        else -> RedisOutput(mutableListOf())
    }
}
