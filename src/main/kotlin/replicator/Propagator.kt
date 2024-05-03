package replicator

import config.Server
import io.ktor.utils.io.writeStringUtf8
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol
import resp.encodeArray

public class Propagator : KoinComponent {
    private val server: Server by inject()

    public suspend fun set(protocol: Protocol) {
        val request = protocol.encodeArray()

        server.replicaClients.forEach { writer ->
            request.forEach {
                writer.writeStringUtf8(it)
            }
        }
    }
}
