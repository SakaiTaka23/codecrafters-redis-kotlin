package commands

import global.RedisCommand
import global.RedisOutput
import kotlin.test.Test
import kotlin.test.assertEquals

public class InfoTest {
    @Test
    public fun `information of replication`() {
        val command = RedisCommand(1, "info", mutableListOf("replication"))
        val result = Info().run(command)

        assertEquals(
            RedisOutput(mutableListOf("role:master")),
            result
        )
    }

    @Test
    public fun `returns empty string on invalid section`() {
        val command = RedisCommand(1, "info", mutableListOf(""))
        val result = Info().run(command)

        assertEquals(
            RedisOutput(mutableListOf()),
            result
        )
    }
}
