package presentor

import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8

public class Responder(
    output: global.RedisOutput,
    private val encoder: Encoder,
    private val sender: ByteWriteChannel
    ) {
    private val commandCount = output.responses.size
    private val responses = output.responses

    public suspend fun sendBulkString() {
        if (commandCount > 1) {
            sender.writeStringUtf8(encoder.resultCount(commandCount))
        }
        responses.forEach {
            sender.writeStringUtf8(encoder.resultContentCount(it.length) + encoder.resultContent(it))
        }
    }

    public suspend fun sendSimpleString() {
        responses.forEach {
            sender.writeStringUtf8(encoder.resultSimpleString(it))
        }
    }
}
