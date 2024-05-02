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
