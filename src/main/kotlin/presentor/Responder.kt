package presentor

import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeStringUtf8
import resp.Protocol
import resp.bulkString
import resp.encodeArray
import resp.rdbFileSize
import resp.simpleString

public class Responder {
    public suspend fun sendBulkString(protocol: Protocol, sender: ByteWriteChannel) {
        val result = protocol.bulkString()
        result.forEach {
            sender.writeStringUtf8(it)
        }
    }

    public suspend fun sendSimpleString(protocol: Protocol, sender: ByteWriteChannel) {
        sender.writeStringUtf8(protocol.simpleString())
    }

    public suspend fun sendRdbFile(content: ByteArray, sender: ByteWriteChannel) {
        sender.writeStringUtf8(content.rdbFileSize())
        sender.writeFully(content)
    }

    public suspend fun sendArray(protocol: Protocol, sender: ByteWriteChannel) {
        val result = protocol.encodeArray()
        result.forEach {
            sender.writeStringUtf8(it)
        }
    }
}
