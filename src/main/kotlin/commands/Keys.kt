package commands

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.Storage
import resp.Protocol

public class Keys : CommandRoutes, KoinComponent {
    private val repo: Storage by inject()

    override fun run(protocol: Protocol): Protocol = Protocol(repo.getAllKey())
}
