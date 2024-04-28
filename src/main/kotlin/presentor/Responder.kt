package presentor

public class Responder(output: global.RedisOutput, private val encoder: Encoder) {
    private val commandCount = output.responses.size
    private val responses = output.responses

    public fun buildResponse(): String {
        val response = buildString {
            append(encoder.resultCount(commandCount))
            responses.forEach {
                append(encoder.resultContentCount(it.length))
                append(encoder.resultContent(it))
            }
        }

        println("Response: $response")
        return response
    }
}
