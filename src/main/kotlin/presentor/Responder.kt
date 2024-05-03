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
        sender.writeStringUtf8(protocol.bulkString())
    }

    public suspend fun sendSimpleString(protocol: Protocol, sender: ByteWriteChannel) {
        sender.writeStringUtf8(protocol.simpleString())
    }

    public suspend fun sendRdbFile(content: ByteArray, sender: ByteWriteChannel) {
        sender.writeStringUtf8(content.rdbFileSize())
        sender.writeFully(content)
    }

    public suspend fun sendRESPArray(protocol: Protocol, sender: ByteWriteChannel) {
        sender.writeStringUtf8(protocol.encodeArray())
    }
}
