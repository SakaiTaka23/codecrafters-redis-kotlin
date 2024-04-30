package commands

import global.RedisCommand
import global.RedisOutput
import java.time.Instant

private const val SET_COMMAND_COUNT = 4

public class Set : CommandRoutes {
    private val repo = repository.InMemory.getInstance()

    override fun run(command: RedisCommand): RedisOutput {
        val expirationTime = checkOption(command)

        repo.set(command.arguments[0], command.arguments[1], Instant.now().plusMillis(expirationTime))

        return RedisOutput(mutableListOf("OK"))
    }

    private fun checkOption(command: RedisCommand): Long {
        if (command.commandCount != SET_COMMAND_COUNT) return -1

        if (command.arguments[2].lowercase() == "px") {
            return command.arguments[3].toLongOrNull() ?: -1
        }
        return -1
    }
}
