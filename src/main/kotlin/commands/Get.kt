package commands

import global.RedisCommand
import global.RedisOutput

public class Get : CommandRoutes {
    private val repo = repository.InMemory.getInstance()

    override suspend fun run(command: RedisCommand): RedisOutput {
        val result = repo.get(command.arguments[0])
        return RedisOutput(mutableListOf(result))
    }
}
