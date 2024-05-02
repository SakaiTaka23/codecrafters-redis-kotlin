package commands

import resp.Protocol
import kotlin.test.Test
import kotlin.test.assertEquals

public class PingTest {
    @Test
    public fun `returns pong`() {
        val command = Protocol(mutableListOf("ping"))
        val result = Ping().run(command)

        assertEquals(
            Protocol(mutableListOf("PONG")),
            result
        )
    }
}
