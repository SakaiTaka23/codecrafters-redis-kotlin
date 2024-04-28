package reciever

import global.RedisCommand
import io.ktor.utils.io.ByteReadChannel

public class Reader(private val source: ByteReadChannel) {
    public suspend fun read(): RedisCommand {
        val commandCount = countCommand()
        println("Command count: $commandCount")
        val mainCommand = MainCommand(source).read()
        println("Main command: $mainCommand")
        val arguments = Arguments(source).read(commandCount)
        println("Arguments: $arguments")

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
