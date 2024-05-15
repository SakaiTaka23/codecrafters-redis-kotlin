package commands

import repository.Storage
import resp.Protocol
import java.time.Clock
import java.time.Instant

public class Set(
    private val repo: Storage,
    private val clock: Clock,
) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol {
        val expirationTime = checkOption(protocol)

        if (expirationTime == null) {
            repo.set(protocol.arguments[1], protocol.arguments[2])
        } else {
            repo.set(protocol.arguments[1], protocol.arguments[2], Instant.now(clock).plusMillis(expirationTime))
        }

        return Protocol(mutableListOf("OK"))
    }

    private fun checkOption(protocol: Protocol): Long? {
        if (protocol.arguments.getOrNull(3)?.lowercase() == "px") {
            return protocol.arguments[4].toLongOrNull()
        }
        return null
    }
}
