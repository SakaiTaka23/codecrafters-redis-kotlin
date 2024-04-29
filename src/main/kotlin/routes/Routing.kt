package routes

import global.RedisCommand
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import presentor.Responder

public class Routing(private val socket: ServerSocket) {
    private val reader = reciever.Reader()
    private val responder = Responder()

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
                            defineRoutes(command, sendChanel)
                        }
                    } catch (e: Throwable) {
                        println("Connection lost $e")
                        connection.close()
                    }
                }
            }
        }
    }

    private suspend fun defineRoutes(command: RedisCommand, sendChannel: ByteWriteChannel) {
        when (command.commandName) {
            "ping" -> {
                val result = commands.Ping().run(command)
                responder.sendSimpleString(result, sendChannel)
            }
            "echo" -> {
                val result = commands.Echo().run(command)
                responder.sendBulkString(result, sendChannel)
            }
            "set" -> {
                val result = commands.Set().run(command)
                responder.sendSimpleString(result, sendChannel)
            }
            else -> error("unknown command ${command.commandName}")
        }
    }
}
