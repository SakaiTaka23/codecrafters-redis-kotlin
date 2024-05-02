package commands

import config.Server
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol

public class Psync : CommandRoutes, KoinComponent {
    private val server: Server by inject()

    override fun run(protocol: Protocol): Protocol =
        if (protocol.arguments[1] == "?" && protocol.arguments[2] == "-1") {
            Protocol(
                mutableListOf(
                    "FULLRESYNC", server.replID, server.replOffset.toString()
                )
            )
        } else {
            Protocol(mutableListOf())
        }
}
