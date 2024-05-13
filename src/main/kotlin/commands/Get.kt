package commands

import repository.Storage
import resp.Protocol

public class Get(private val repo: Storage) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol = Protocol(mutableListOf(repo.get(protocol.arguments[1]) ?: "-1"))
}
