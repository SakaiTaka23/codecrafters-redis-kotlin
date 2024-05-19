package presentor

import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeStringUtf8
import resp.Protocol
import resp.bulkString
import resp.encodeArray
import resp.integer
import resp.rdbFileSize
import resp.simpleError
import resp.simpleString

public object Responder {
    public suspend fun sendBulkString(protocol: Protocol, sender: ByteWriteChannel) {
        val result = protocol.bulkString()
        result.forEach {
            sender.writeStringUtf8(it)
        }
    }

    public suspend fun sendArray(protocol: Protocol, sender: ByteWriteChannel) {
        val result = protocol.encodeArray()
        result.forEach {
            sender.writeStringUtf8(it)
        }
    }

    public suspend fun sendRdbFile(content: ByteArray, sender: ByteWriteChannel) {
        sender.writeStringUtf8(content.rdbFileSize())
        sender.writeFully(content)
    }

    public suspend fun sendSimpleError(protocol: Protocol, sender: ByteWriteChannel) {
        sender.writeStringUtf8(protocol.simpleError())
    }

    public suspend fun sendSimpleString(protocol: Protocol, sender: ByteWriteChannel) {
        sender.writeStringUtf8(protocol.simpleString())
    }

    public suspend fun sendInteger(protocol: Protocol, sender: ByteWriteChannel) {
        sender.writeStringUtf8(protocol.integer())
    }
}
