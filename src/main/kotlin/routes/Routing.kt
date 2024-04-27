package routes

import global.ParsedCommand
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8

public class Routing {
    public suspend fun defineRoutes(command: ParsedCommand, sendChannel: ByteWriteChannel) {
        if (command.mainCommand == "ping") {
            sendChannel.writeStringUtf8(commands.Ping().run)
        } else if (command.mainCommand == "echo") {
            sendChannel.writeStringUtf8(commands.Echo().run(command.subArg!!))
        }
    }
}
