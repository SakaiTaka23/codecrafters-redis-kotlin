package commands

import global.RedisCommand
import global.RedisOutput
import java.time.Instant
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

public class Set : CommandRoutes, KoinComponent {
    private val repo: repository.IStorage by inject()

    override fun run(command: RedisCommand): RedisOutput {
        val expirationTime = checkOption(command)

        println("expirationTime $expirationTime")
        if (expirationTime == null) {
            repo.set(command.arguments[0], command.arguments[1])
        } else {
            repo.set(command.arguments[0], command.arguments[1], Instant.now().plusMillis(expirationTime))
        }

        return RedisOutput(mutableListOf("OK"))
    }

    private fun checkOption(command: RedisCommand): Long? {
        if (command.arguments.getOrNull(2)?.lowercase() == "px") {
            return command.arguments[3].toLongOrNull()
        }
        return null
    }
}
