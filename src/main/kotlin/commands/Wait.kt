package commands

import config.Server
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol

public class Wait : CommandRoutes, KoinComponent {
    private val server: Server by inject()
    override fun run(protocol: Protocol): Protocol = Protocol(mutableListOf(server.getReplicaCount().toString()))
}
