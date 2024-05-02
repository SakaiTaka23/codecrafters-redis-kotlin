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
            var result = ""
            if (server.isSlave) {
                result += "role:slave"
            } else {
                result += "role:master\r\n"
                result += "master_replid:8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb\r\n"
                result += "master_repl_offset:0\r\n"
            }
            RedisOutput(mutableListOf(result))
        }

        else -> RedisOutput(mutableListOf())
    }
}
