package commands

import config.Server
import resp.Protocol

public class Info(private val server: Server) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol {
        val result = mutableListOf<String>()

        return when (protocol.arguments.getOrNull(1)) {
            "replication" -> {
                if (server.isSlave) {
                    result.add("role:slave")
                } else {
                    result.add(
                        "role:master\r\n"
                                + "master_replid:${server.replID}\r\n"
                                + "master_repl_offset:${server.replOffset}"
                    )
                }
                Protocol(result)
            }

            else -> Protocol(mutableListOf())
        }
    }
}
