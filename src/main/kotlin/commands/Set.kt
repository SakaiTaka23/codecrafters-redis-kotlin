package commands

import global.RedisCommand
import global.RedisOutput
import java.time.Instant

public class Set : CommandRoutes {
    private val repo = repository.InMemory.getInstance()

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
