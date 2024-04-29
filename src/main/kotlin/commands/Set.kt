package commands

import global.RedisCommand
import global.RedisOutput
import kotlinx.coroutines.coroutineScope

public class Set : CommandRoutes {
    private val repo = repository.InMemory.getInstance()

    override suspend fun run(command: RedisCommand): RedisOutput {
        coroutineScope {
            repo.set(command.arguments[0], command.arguments[1])
        }
        return RedisOutput(mutableListOf("OK"))
    }
}
