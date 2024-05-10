package replicator

import config.Server
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
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

    public suspend fun run() {
        createClient()
        sendPING()
        sendREPLCONF()
        sendPSYNC()
        println("finished handshake successfully")
    }

    private suspend fun createClient() {
        val hostname = checkNotNull(server.masterHost) { "Illegal State: Hostname should not be null" }
        val port = checkNotNull(server.masterPort) { "Illegal State: Port should not be null" }

        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect(hostname, port)
        server.masterWriter = socket.openWriteChannel(autoFlush = true)
        server.masterReader = socket.openReadChannel()
    }

    private suspend fun sendPING() {
        client.sendArray(Protocol(mutableListOf("ping")), server.masterWriter)
        if (reader.read(server.masterReader).arguments[0] != "PONG") {
            HANDSHAKE_ERROR
        }
    }

    private suspend fun sendREPLCONF() {
        client.sendArray(
            Protocol(mutableListOf("REPLCONF", "listening-port", "${server.port}")), server.masterWriter
        )
        if (!reader.read(server.masterReader).isOK()) {
            HANDSHAKE_ERROR
        }
        client.sendArray(Protocol(mutableListOf("REPLCONF", "capa", "psync2")), server.masterWriter)
        if (!reader.read(server.masterReader).isOK()) {
            HANDSHAKE_ERROR
        }
    }

    private suspend fun sendPSYNC() {
        server.masterReader = server.masterReader
        server.masterWriter = server.masterWriter
        client.sendArray(Protocol(mutableListOf("PSYNC", "?", "-1")), server.masterWriter)
        val result = reader.read(server.masterReader).arguments[0]
        if (!result.startsWith("FULLRESYNC")) {
            HANDSHAKE_ERROR
        }
        // read rdb file
        reader.readRdb(server.masterReader)
    }
}
