package commands

import config.Server
import resp.Protocol

public class Config(private val server: Server) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol {
        val result = mutableListOf<String>()

        protocol.arguments.forEach {
            when (it) {
                "dir" -> result.apply {
                    add("dir")
                    add(server.dir)
                }
                "dbname" -> result.apply {
                    add("dbname")
                    add(server.dbfilename)
                }
            }
        }

        return Protocol(result)
    }
}
