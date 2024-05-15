package replicator

import config.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import presentor.Responder
import resp.Protocol
import resp.countBytes

public class Propagator(
    private val server: Server,
    private val sender: Responder,
    private val propagateCommandChannel: Channel<Protocol>,
) {
    private val ackProtocol = Protocol(mutableListOf("REPLCONF", "GETACK", "*"))

    public suspend fun run() {
        while (true) {
            val protocol = propagateCommandChannel.receive()
            server.replicaClients.forEach { client ->
                sender.sendArray(protocol, client.writer)
            }
            println("adding on set command ${protocol.countBytes()}")
            server.replOffset += protocol.countBytes()
        }
    }

    public fun CoroutineScope.sendAck() {
        launch {
            server.replicaClients.map { client ->
                sender.sendArray(ackProtocol, client.writer)
            }
            server.replOffset += ackProtocol.countBytes()
        }
    }
}
