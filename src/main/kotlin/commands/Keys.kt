package commands

import repository.Storage
import resp.Protocol

public class Keys(private val repo: Storage) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol = Protocol(repo.getAllKey())
}
