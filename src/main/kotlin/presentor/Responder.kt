package presentor

import global.RedisOutput
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8

public class Responder(private val encoder: Encoder) {
    public suspend fun sendBulkString(output: RedisOutput, sender: ByteWriteChannel) {
        if (output.responses[0] == "-1") {
            sender.writeStringUtf8(encoder.resultNullBulkString())
            return
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

    public suspend fun sendRESPArray(output: RedisOutput, sender: ByteWriteChannel) {
        var response: String
        val commandCount = output.responses.size
        response = encoder.resultCommandCount(commandCount)
        output.responses.forEach {
            response += encoder.resultContentCount(it.length) + encoder.resultContent(it)
        }
        sender.writeStringUtf8(response)
    }
}
