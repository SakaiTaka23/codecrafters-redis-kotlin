package replicator

import config.Server
import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentor.Responder
import resp.Protocol

public class Propagator : KoinComponent {
    private val server: Server by inject()
    private val client: Responder by inject()
    private val propagateCommandChannel: Channel<Protocol> by inject()

    public suspend fun run() {
        while (true) {
            val protocol = propagateCommandChannel.receive()
            server.replicaClients.forEach { writer ->
                client.sendArray(protocol, writer)
            }
        }
    }
}
