package commands

import global.RedisCommand
import global.RedisOutput
import kotlin.test.Test
import kotlin.test.assertEquals

public class PingTest {
    @Test
    public fun `returns pong`() {
        val command = RedisCommand(1, "ping", mutableListOf())
        val result = Ping().run(command)

        assertEquals(
            RedisOutput(mutableListOf("PONG")),
            result
        )
    }
}
