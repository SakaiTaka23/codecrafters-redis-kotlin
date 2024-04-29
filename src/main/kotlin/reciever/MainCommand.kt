package reciever

import io.ktor.utils.io.ByteReadChannel

public class MainCommand {
    public suspend fun read(source: ByteReadChannel): String {
        val lineCount = source.readUTF8Line(10)?.removePrefix("$")?.toIntOrNull() ?: error("closed")
        val command = source.readUTF8Line(10)?.lowercase() ?: error("closed")

        if (command.length != lineCount) {
            error("Invalid command")
        }

        return command
    }
}
