package routes

import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8

public class Routing {
    public suspend fun defineRoutes(command: String?, sendChannel: ByteWriteChannel) {
        if (command == "PING") {
            sendChannel.writeStringUtf8(commands.Ping().run)
        }
    }
}
