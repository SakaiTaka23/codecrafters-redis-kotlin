package commands

import global.RedisCommand
import global.RedisOutput

public class Set: CommandRoutes {
    private val repo = repository.InMemory.getInstance()
    override fun run(command: RedisCommand): RedisOutput {
        repo.set(command.arguments[0], command.arguments[1])
        return RedisOutput(mutableListOf("OK"))
    }
}
