package commands

import config.Server
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol

public class Config: CommandRoutes, KoinComponent {
    private val server: Server by inject()

    override fun run(protocol: Protocol): Protocol {
        val result: MutableList<String> = mutableListOf()

        protocol.arguments.forEach {
            when (it) {
                "dir" -> {
                    result.add("dir")
                    result.add(server.dir)
                }
                "dbfilename" -> {
                    result.add("dbfilename")
                    result.add(server.dbfilename)
                }
            }
        }

        return Protocol(result)
    }
}
