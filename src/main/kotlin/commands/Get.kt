package commands

import global.RedisCommand
import global.RedisOutput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

public class Get : CommandRoutes, KoinComponent {
    private val repo: repository.IStorage by inject()

    override fun run(command: RedisCommand): RedisOutput {
        val result = repo.get(command.arguments[0]) ?: "-1"
        return RedisOutput(mutableListOf(result))
    }
}
