package reciever

import io.ktor.utils.io.ByteReadChannel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Decoder
import resp.Protocol

private const val READ_LINE_LIMIT = 50

public class Reader : KoinComponent {
    private val decoder: Decoder by inject()

    public suspend fun read(source: ByteReadChannel): Protocol {
        val result = mutableListOf<String>()
        val firstLine = source.readUTF8Line(READ_LINE_LIMIT) ?: error("message not found")
        val firstLetter = firstLine.first()

        return when (firstLetter) {
            '+' -> {
                result.add(decoder.read(firstLine))
                println("got protocol $result")
                Protocol(result)
            }

            '*' -> {
                val inputSize = firstLine.removePrefix("*").toIntOrNull() ?: error("invalid command count")
                repeat(inputSize * 2) {
                    val line = source.readUTF8Line(READ_LINE_LIMIT) ?: error("input size exceeds input size")
                    decoder.read(line).let {
                        if (it.isNotBlank()) {
                            result.add(it)
                        }
                    }
                }
                result[0] = result[0].lowercase()
                println("got protocol $result")
                Protocol(result)
            }

            else -> Protocol(result)
        }
    }
}
