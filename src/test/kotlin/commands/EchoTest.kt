package commands

import global.RedisCommand
import global.RedisOutput
import kotlin.test.Test
import kotlin.test.assertEquals

private const val ECHO_STR = "apple"

public class EchoTest {
    @Test
    public fun `returns expected words`() {
        val input = RedisCommand(1, "echo", mutableListOf(ECHO_STR))
        val result = Echo().run(input)

        assertEquals(RedisOutput(mutableListOf(ECHO_STR)), result)
    }
}
