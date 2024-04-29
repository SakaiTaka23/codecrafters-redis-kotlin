package commands

import global.RedisCommand
import global.RedisOutput
import kotlinx.coroutines.coroutineScope

public class Set : CommandRoutes {
    private val repo = repository.InMemory.getInstance()

    override suspend fun run(command: RedisCommand): RedisOutput {
        val expirationTime = checkOption(command)

        coroutineScope {
            repo.set(command.arguments[0], command.arguments[1], expirationTime)
        }
        return RedisOutput(mutableListOf("OK"))
    }

    private fun checkOption(command: RedisCommand): Long {
        if (command.commandCount != 5) return -1

        if (command.arguments[2].lowercase() == "px") {
            return command.arguments[3].toLongOrNull() ?: -1
        }
        return -1
    }
}
