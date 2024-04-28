package commands

import global.RedisCommand
import global.RedisOutput

public interface CommandRoutes {
    public fun run(command: RedisCommand): RedisOutput
}
