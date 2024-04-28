package routes

import global.RedisCommand
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8

public class Routing {
    public suspend fun defineRoutes(command: RedisCommand, sendChannel: ByteWriteChannel) {
        if (command.commandName == "ping") {
            val result = commands.Ping().run(command)
            val response = presentor.Responder(result, presentor.Encoder()).buildResponse()
            sendChannel.writeStringUtf8(response)
        } else if (command.commandName == "echo") {
            val result = commands.Echo().run(command)
            val response = presentor.Responder(result, presentor.Encoder()).buildResponse()
            sendChannel.writeStringUtf8(response)
        }
    }
}
