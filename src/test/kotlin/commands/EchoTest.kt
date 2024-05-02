package commands

import resp.Protocol
import kotlin.test.Test
import kotlin.test.assertEquals

private const val ECHO_STR = "apple"

public class EchoTest {
    @Test
    public fun `returns expected words`() {
        val input = Protocol(mutableListOf("echo", ECHO_STR))
        val result = Echo().run(input)

        assertEquals(Protocol(mutableListOf(ECHO_STR)), result)
    }
}
