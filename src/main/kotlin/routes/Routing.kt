package routes

import global.RedisCommand
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8

public class Routing {
    public suspend fun defineRoutes(command: RedisCommand, sendChannel: ByteWriteChannel) {
        if (command.commandName == "ping") {
            sendChannel.writeStringUtf8(commands.Ping().run)
        } else if (command.commandName == "echo") {
            sendChannel.writeStringUtf8(commands.Echo().run(command.arguments[0]))
        }
    }
}
