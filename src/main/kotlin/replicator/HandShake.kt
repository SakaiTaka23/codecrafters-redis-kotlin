package replicator

import config.Server
import global.RedisOutput
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentor.Responder

public class HandShake : KoinComponent {
    private val server: Server by inject()
    private val client: Responder by inject()
    private lateinit var channel: ByteWriteChannel

    public suspend fun run() {
        createClient()
        sendPING()
    }

    private suspend fun createClient() {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect(server.masterHost!!, server.masterPort!!)
        channel = socket.openWriteChannel(autoFlush = true)
    }

    private suspend fun sendPING() {
        client.sendRESP(RedisOutput(mutableListOf("ping")), channel)
    }
}
