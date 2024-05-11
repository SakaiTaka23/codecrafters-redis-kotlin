package commands

import config.Server
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol

public class Config : CommandRoutes, KoinComponent {
    private val server: Server by inject()

    override fun run(protocol: Protocol): Protocol {
        val result = mutableListOf<String>()

        protocol.arguments.forEach {
            when (it) {
                "dir" -> result.apply { add("dir"); add(server.dir) }
                "dbname" -> result.apply { add("dbname"); add(server.dbfilename) }
            }
        }

        return Protocol(result)
    }
}
