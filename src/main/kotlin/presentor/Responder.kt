package presentor

import global.RedisOutput
import global.commandCount
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8

public class Responder {
    private val encoder = Encoder()

    public suspend fun sendBulkString(output: RedisOutput, sender: ByteWriteChannel) {
        val commandCount = output.commandCount()

        if (commandCount > 1) {
            sender.writeStringUtf8(encoder.resultCount(commandCount))
        }
        output.responses.forEach {
            sender.writeStringUtf8(encoder.resultContentCount(it.length) + encoder.resultContent(it))
        }
    }

    public suspend fun sendSimpleString(output: RedisOutput, sender: ByteWriteChannel) {
        output.responses.forEach {
            sender.writeStringUtf8(encoder.resultSimpleString(it))
        }
    }
}
