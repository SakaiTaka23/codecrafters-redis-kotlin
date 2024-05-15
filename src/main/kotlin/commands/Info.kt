package commands

import config.Server
import resp.Protocol

public class Info(private val server: Server) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol = when (protocol.arguments.getOrNull(1)) {
        "replication" -> {
            if (server.isSlave) {
                Protocol(mutableListOf("role:slave"))
            } else {
                Protocol(
                    mutableListOf(
                        "role:master\r\n" +
                            "master_replid:${server.replID}\r\n" +
                            "master_repl_offset:${server.replOffset}",
                    ),
                )
            }
        }

        else -> Protocol(mutableListOf())
    }
}
