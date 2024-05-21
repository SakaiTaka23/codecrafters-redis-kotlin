package routes

import config.Server
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import presentor.Responder
import reciever.Reader
import replicator.Propagator
import repository.Storage
import repository.StreamStorage
import resp.Protocol
import java.time.Clock

@Suppress("LongParameterList")
public class Routing(
    private val propagateChannel: Channel<Protocol>,
    private val propagator: Propagator,
    private val reader: Reader,
    private val repo: Storage,
    private val responder: Responder,
    private val server: Server,
    private val socket: ServerSocket,
    private val streamRepo: StreamStorage,
) {
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
                            masterRoutes(command, receiveChannel, sendChanel)
                            server.lastCommand = command
                        }
                    } catch (e: Throwable) {
                        println("Connection lost $e")
                        connection.close()
                    }
                }
            }
        }
    }

    private suspend fun masterRoutes(
        protocol: Protocol,
        receiveChannel: ByteReadChannel,
        sendChannel: ByteWriteChannel,
    ) {
        when (protocol.arguments[0]) {
            "config" -> {
                val result = commands.Config(server).run(protocol)
                responder.sendArray(result, sendChannel)
            }

            "echo" -> {
                val result = commands.Echo().run(protocol)
                responder.sendBulkString(result, sendChannel)
            }

            "get" -> {
                val result = commands.Get(repo).run(protocol)
                responder.sendBulkString(result, sendChannel)
            }

            "info" -> {
                val result = commands.Info(server).run(protocol)
                responder.sendBulkString(result, sendChannel)
            }

            "keys" -> {
                val result = commands.Keys(repo).run(protocol)
                responder.sendArray(result, sendChannel)
            }

            "ping" -> {
                val result = commands.Ping().run(protocol)
                responder.sendSimpleString(result, sendChannel)
            }

            "psync" -> {
                val result = commands.Psync(server).run(protocol)
                responder.sendSimpleString(result, sendChannel)
                commands.Psync(server).saveClient(receiveChannel, sendChannel)
                val rdbFile = commands.Psync(server).emptyRDBFile()
                responder.sendRdbFile(rdbFile, sendChannel)
            }

            "replconf" -> {
                val result = commands.Replconf(server).run(protocol)
                if (result.arguments.getOrNull(0) == "OK") {
                    responder.sendSimpleString(result, sendChannel)
                }
            }

            "set" -> {
                val result = commands.Set(repo, Clock.systemUTC()).run(protocol)
                responder.sendSimpleString(result, sendChannel)
                propagateChannel.send(protocol)
            }

            "type" -> {
                val result = commands.Type(repo, streamRepo).run(protocol)
                responder.sendSimpleString(result, sendChannel)
            }

            "wait" -> {
                val result = commands.Wait(server, propagator).run(protocol)
                responder.sendInteger(result, sendChannel)
            }

            "xadd" -> {
                val result = commands.Xadd(streamRepo).run(protocol)
                if (result.arguments[0].startsWith("ERR")) {
                    responder.sendSimpleError(result, sendChannel)
                } else {
                    responder.sendBulkString(result, sendChannel)
                }
            }

            "xrange" -> {
                val result = commands.XRange(streamRepo).run(protocol)
                responder.sendList(result, sendChannel)
            }

            "xread" -> {
                val result = commands.XRead(streamRepo).run(protocol)
                responder.sendStreamList(result, sendChannel)
            }

            else -> error("unknown command ${protocol.arguments[0]}")
        }
    }
}
