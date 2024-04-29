package commands

import global.RedisCommand
import global.RedisOutput

public interface CommandRoutes {
    public suspend fun run(command: RedisCommand): RedisOutput
}
