package commands

import config.Server
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import java.util.Base64
import resp.Protocol

@Suppress("MaxLineLength")
private const val EMPTY_RDB_FILE =
    "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog=="

public class Psync(private val server: Server) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol =
        if (protocol.arguments[1] == "?" && protocol.arguments[2] == "-1") {
            Protocol(
                mutableListOf(
                    "FULLRESYNC", server.replID, server.replOffset.toString()
                )
            )
        } else {
            Protocol(mutableListOf())
        }

    public fun saveClient(reader: ByteReadChannel, writer: ByteWriteChannel): Unit =
        server.addReplicaChannel(reader, writer)

    public fun emptyRDBFile(): ByteArray = Base64.getDecoder().decode(EMPTY_RDB_FILE)
}
