package commands

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol

public class Get : CommandRoutes, KoinComponent {
    private val repo: repository.Storage by inject()

    override fun run(protocol: Protocol): Protocol = Protocol(mutableListOf(repo.get(protocol.arguments[1]) ?: "-1"))
}
