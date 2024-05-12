package commands

import config.Replica
import config.Server
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol
import kotlin.math.max

private const val GETACK_BYTE_SIZE = 37
private const val HANDSHAKE_BYTE_SIZE = 166

public class Replconf : KoinComponent {
    private val server: Server by inject()

    public suspend fun run(protocol: Protocol): Protocol {
        if (protocol.arguments[1] == "ACK") {
            server.propagateResultChannel.send(protocol.arguments[2].toInt())
            return Protocol(mutableListOf())
        }

        return Protocol(mutableListOf("OK"))
    }
}

public class ReplconfAck : CommandRoutes, KoinComponent {
    private val replica = Replica.getInstance()

    override fun run(protocol: Protocol): Protocol {
        if (protocol.arguments[1] == "GETACK") {
            val offset = max(replica.getOffset() - HANDSHAKE_BYTE_SIZE - GETACK_BYTE_SIZE, 0)
            val offsetStr = offset.toString()
            return Protocol(mutableListOf("REPLCONF", "ACK", offsetStr))
        } else {
            error("invalid command")
        }
    }
}
