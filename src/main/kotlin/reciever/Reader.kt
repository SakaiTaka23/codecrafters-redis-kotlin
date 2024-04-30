package reciever

import global.RedisCommand
import io.ktor.utils.io.ByteReadChannel

public class Reader(private val mainCommandReader: MainCommand, private val argReader: Arguments) {
    public suspend fun read(source: ByteReadChannel): RedisCommand {
        val commandCount = countCommand(source)
        val mainCommand = mainCommandReader.read(source)
        val arguments = argReader.read(source, commandCount)

        val result = RedisCommand(commandCount, mainCommand, arguments)
        println("Command: $result")
        return result
    }

    private suspend fun countCommand(source: ByteReadChannel): Int {
        val countLine = source.readUTF8Line(10) ?: ""
        if (countLine.startsWith("*")) {
            return countLine.removePrefix("*").toInt()
        }
        return 0
    }
}
