package routes

import global.RedisCommand
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import presentor.Responder
import reciever.Reader

public class Routing(
    private val reader: Reader,
    private val responder: Responder,
    private val socket: ServerSocket
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
            "echo" -> {
                val result = commands.Echo().run(command)
                responder.sendBulkString(result, sendChannel)
            }

            "get" -> {
                val result = commands.Get().run(command)
                responder.sendBulkString(result, sendChannel)
            }

            "info" -> {
                val result = commands.Info().run(command)
                responder.sendBulkString(result, sendChannel)
            }

            "ping" -> {
                val result = commands.Ping().run(command)
                responder.sendSimpleString(result, sendChannel)
            }


            "set" -> {
                val result = commands.Set().run(command)
                responder.sendSimpleString(result, sendChannel)
            }


            else -> error("unknown command ${command.commandName}")
        }
    }
}
