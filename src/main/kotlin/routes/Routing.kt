package routes

import config.Server
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentor.Responder
import reciever.Reader
import replicator.Propagator
import resp.Protocol

public class Routing(private val socket: ServerSocket) : KoinComponent {
    private val propagator: Propagator by inject()
    private val reader: Reader by inject()
    private val responder: Responder by inject()
    private val server: Server by inject()

    public suspend fun start() {
        coroutineScope {
            while (true) {
                val connection = socket.accept()
                launch {
                    val receiveChannel = connection.openReadChannel()
                    val sendChanel = connection.openWriteChannel(autoFlush = true)
                    try {
                        while (true) {
                            val command = reader.read(receiveChannel)
                            masterRoutes(command, sendChanel)
                        }
                    } catch (e: Throwable) {
                        println("Connection lost $e")
                        connection.close()
                    }
                }
            }
        }
    }

    public suspend fun readPropagate() {
        coroutineScope {
            launch {
                try {
                    while (true) {
                        val command = reader.read(server.masterReader)
                        propagateRoutes(command, server.masterWriter)
                    }
                } catch (e: Throwable) {
                    println("Connection lost with master $e")
                }
            }
        }
    }

    private suspend fun masterRoutes(protocol: Protocol, sendChannel: ByteWriteChannel) {
        when (protocol.arguments[0]) {
            "echo" -> {
                val result = commands.Echo().run(protocol)
                responder.sendBulkString(result, sendChannel)
            }

            "get" -> {
                val result = commands.Get().run(protocol)
                responder.sendBulkString(result, sendChannel)
            }

            "info" -> {
                val result = commands.Info().run(protocol)
                responder.sendBulkString(result, sendChannel)
            }

            "ping" -> {
                val result = commands.Ping().run(protocol)
                responder.sendSimpleString(result, sendChannel)
            }

            "psync" -> {
                val result = commands.Psync().run(protocol)
                responder.sendSimpleString(result, sendChannel)
                commands.Psync().saveClient(sendChannel)
                val rdbFile = commands.Psync().emptyRDBFile()
                responder.sendRdbFile(rdbFile, sendChannel)
            }

            "replconf" -> {
                val result = commands.Replconf().run(protocol)
                responder.sendSimpleString(result, sendChannel)
            }

            "set" -> {
                val result = commands.Set().run(protocol)
                responder.sendSimpleString(result, sendChannel)
                propagator.set(protocol)
            }


            else -> error("unknown command ${protocol.arguments[0]}")
        }
    }

    private suspend fun propagateRoutes(protocol: Protocol, sendChannel: ByteWriteChannel) {
        when (protocol.arguments[0]) {
            "replconf" -> {
                val result = commands.ReplconfAck().run(protocol)
                responder.sendArray(result, sendChannel)
            }

            "set" -> {
                commands.Set().run(protocol)
            }

            else -> error("unknown command ${protocol.arguments[0]}")
        }
    }
}
