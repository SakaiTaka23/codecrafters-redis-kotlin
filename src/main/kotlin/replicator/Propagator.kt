package replicator

import config.Server
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentor.Responder
import resp.Protocol


public class Propagator : KoinComponent {
    private val ackProtocol = Protocol(mutableListOf("REPLCONF", "GETACK", "*"))
    private val server: Server by inject()
    private val sender: Responder by inject()
    private val propagateCommandChannel: Channel<Protocol> by inject()

    public suspend fun run() {
        while (true) {
            val protocol = propagateCommandChannel.receive()
            server.replicaClients.forEach { client ->
                sender.sendArray(protocol, client.writer)
            }
        }
    }

    public suspend fun sendAck() {
        coroutineScope {
            launch {
                server.replicaClients.map { client ->
                    sender.sendArray(ackProtocol, client.writer)
                }
            }
        }
    }
}
