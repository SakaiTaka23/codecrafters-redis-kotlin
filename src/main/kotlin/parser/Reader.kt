package parser

import io.ktor.utils.io.ByteReadChannel

public class Reader(private val source: ByteReadChannel) {
    public suspend fun read(): String {
        val items = source.readUTF8Line(10)?.toIntOrNull() ?: error("closed")

        val values = (0 until items).map {
            read()
        }

        return values.joinToString("\r\n")
    }
}
