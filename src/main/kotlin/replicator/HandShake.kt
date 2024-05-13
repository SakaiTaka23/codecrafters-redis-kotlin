package replicator

import config.Replica
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import presentor.Responder
import reciever.Reader
import resp.Protocol
import resp.isOK

private object HandshakeError : Throwable("Failed to create connection with master node")

public class HandShake(
    private val serverPort: Int,
    private val client: Responder,
    private val reader: Reader
) {
    public suspend fun run(masterHost: String?, masterPort: Int?): Replica {
        val masterConnection = createClient(masterHost, masterPort)
        sendPING(masterConnection)
        sendREPLCONF(masterConnection)
        sendPSYNC(masterConnection)
        println("finished handshake successfully")

        return masterConnection
    }

    private suspend fun createClient(masterHost: String?, masterPort: Int?): Replica {
        val hostname = checkNotNull(masterHost) { "Illegal State: Hostname should not be null" }
        val port = checkNotNull(masterPort) { "Illegal State: Port should not be null" }

        val selectorManager = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selectorManager).tcp().connect(hostname, port)
        return Replica(
            socket.openReadChannel(),
            socket.openWriteChannel(autoFlush = true)
        )
    }

    private suspend fun sendPING(masterConnection: Replica) {
        client.sendArray(Protocol(mutableListOf("ping")), masterConnection.writer)
        if (reader.read(masterConnection.reader).arguments[0] != "PONG") {
            throw HandshakeError
        }
    }

    private suspend fun sendREPLCONF(masterConnection: Replica) {
        client.sendArray(
            Protocol(mutableListOf("REPLCONF", "listening-port", "${serverPort}")), masterConnection.writer
        )
        if (!reader.read(masterConnection.reader).isOK()) {
            throw HandshakeError
        }
        client.sendArray(Protocol(mutableListOf("REPLCONF", "capa", "psync2")), masterConnection.writer)
        if (!reader.read(masterConnection.reader).isOK()) {
            throw HandshakeError
        }
    }

    private suspend fun sendPSYNC(masterConnection: Replica) {
        client.sendArray(Protocol(mutableListOf("PSYNC", "?", "-1")), masterConnection.writer)
        val result = reader.read(masterConnection.reader).arguments[0]
        if (!result.startsWith("FULLRESYNC")) {
            throw HandshakeError
        }
        // read rdb file
        reader.readRdb(masterConnection.reader)
    }
}
