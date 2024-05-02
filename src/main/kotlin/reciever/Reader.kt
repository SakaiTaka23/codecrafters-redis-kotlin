package reciever

import io.ktor.utils.io.ByteReadChannel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Decoder
import resp.Protocol

public class Reader : KoinComponent {
    private val decoder: Decoder by inject()

    public suspend fun read(source: ByteReadChannel): Protocol {
        val result = mutableListOf<String>()
        val firstLine = source.readUTF8Line(10) ?: error("message not found")
        val firstLetter = firstLine.first()

        return when (firstLetter) {
            '+' -> {
                result.add(decoder.read(firstLine))
                Protocol(result)
            }

            '*' -> {
                val inputSize = firstLine.removePrefix("*").toIntOrNull() ?: error("invalid command count")
                repeat(inputSize * 2) {
                    val line = source.readUTF8Line(10) ?: error("input size exceeds input size")
                    decoder.read(line).let {
                        if (it.isNotBlank()) {
                            result.add(it)
                        }
                    }
                }
                Protocol(result)
            }

            else -> Protocol(result)
        }
    }
}
