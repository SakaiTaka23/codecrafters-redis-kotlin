package commands

import repository.Storage
import resp.Protocol

public class Type(private val repo: Storage) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol {
        val value = repo.get(protocol.arguments[1])
        return if (value == null) {
            Protocol(mutableListOf("none"))
        } else {
            Protocol(mutableListOf("string"))
        }
    }
}
