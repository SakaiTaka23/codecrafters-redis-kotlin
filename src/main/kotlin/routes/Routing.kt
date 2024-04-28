package routes

import global.RedisCommand
import io.ktor.utils.io.ByteWriteChannel

public class Routing {
    public suspend fun defineRoutes(command: RedisCommand, sendChannel: ByteWriteChannel) {
        if (command.commandName == "ping") {
            val result = commands.Ping().run(command)
            presentor.Responder(result, presentor.Encoder(), sendChannel).sendResponse()
        } else if (command.commandName == "echo") {
            val result = commands.Echo().run(command)
            presentor.Responder(result, presentor.Encoder(), sendChannel).sendResponse()
        }
    }
}
