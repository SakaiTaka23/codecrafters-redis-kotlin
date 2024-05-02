package commands

import config.Server
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol

public class Info : CommandRoutes, KoinComponent {
    private val server: Server by inject()

    override fun run(protocol: Protocol): Protocol {
        val result = mutableListOf<String>()

        return when (protocol.arguments.getOrNull(1)) {
            "replication" -> {
                if (server.isSlave) {
                    result.add("role:slave")
                } else {
                    result.add("role:master")
                    result.add("master_replid:8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb")
                    result.add("master_repl_offset:0")
                }
                Protocol(result)
            }

            else -> Protocol(mutableListOf())
        }
    }
}
