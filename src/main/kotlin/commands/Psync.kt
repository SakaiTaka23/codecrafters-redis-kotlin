package commands

import config.Server
import java.util.Base64
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol

@Suppress("MaxLineLength")
private const val EMPTY_RDB_FILE =
    "UkVESVMwMDEx+glyZWRpcy12ZXIFNy4yLjD6CnJlZGlzLWJpdHPAQPoFY3RpbWXCbQi8ZfoIdXNlZC1tZW3CsMQQAPoIYW9mLWJhc2XAAP/wbjv+wP9aog=="

public class Psync : CommandRoutes, KoinComponent {
    private val server: Server by inject()

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

    public fun emptyRDBFile(): ByteArray = Base64.getDecoder().decode(EMPTY_RDB_FILE)
}
