package replicator

import config.Server
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentor.Responder
import reciever.Reader
import resp.Protocol
import resp.isOK

private val HANDSHAKE_ERROR: Nothing = error("Failed to create connection with master node")

public class HandShake : KoinComponent {
    private val server: Server by inject()
    private val client: Responder by inject()
    private val reader: Reader by inject()
    private lateinit var writeChannel: ByteWriteChannel
    private lateinit var readChanel: ByteReadChannel

    public suspend fun run() {
        createClient()
        sendPING()
        sendREPLCONF()
    }

    private suspend fun createClient() {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect(server.masterHost!!, server.masterPort!!)
        writeChannel = socket.openWriteChannel(autoFlush = true)
        readChanel = socket.openReadChannel()
    }

    private suspend fun sendPING() {
        client.sendRESPArray(Protocol(mutableListOf("ping")), writeChannel)
        if (reader.read(readChanel).arguments[0] != "PONG") {
            HANDSHAKE_ERROR
        }
    }

    private suspend fun sendREPLCONF() {
        client.sendRESPArray(
            Protocol(mutableListOf("REPLCONF", "listening-port", "${server.port}")), writeChannel
        )
        if (!reader.read(readChanel).isOK()) {
            HANDSHAKE_ERROR
        }
        client.sendRESPArray(Protocol(mutableListOf("REPLCONF", "capa", "psync2")), writeChannel)
        if (!reader.read(readChanel).isOK()) {
            HANDSHAKE_ERROR
        }
    }
}