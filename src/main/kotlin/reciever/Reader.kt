package reciever

import global.RedisCommand
import io.ktor.utils.io.ByteReadChannel

public class Reader(private val source: ByteReadChannel) {
    public suspend fun read(): RedisCommand {
        val commandCount = countCommand()
        val mainCommand = MainCommand(source).read()
        val arguments = Arguments(source).read(commandCount)

        val result = RedisCommand(commandCount,mainCommand, arguments)
        println("Command: $result")
        return result
    }

    private suspend fun countCommand(): Int {
        val countLine = source.readUTF8Line(10) ?: ""
        if (countLine.startsWith("*")) {
            return countLine.removePrefix("*").toInt()
        }
        return 0
    }
}
