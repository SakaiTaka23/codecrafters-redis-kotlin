package commands

import java.time.Clock
import java.time.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import resp.Protocol

public class Set : CommandRoutes, KoinComponent {
    private val repo: repository.IStorage by inject()
    private val clock: Clock by inject()

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
