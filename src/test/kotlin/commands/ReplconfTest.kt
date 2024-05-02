package commands

import resp.Protocol
import kotlin.test.Test
import kotlin.test.assertEquals

public class ReplconfTest {
    @Test
    public fun `returns ok`() {
        val protocol = Protocol(mutableListOf("REPLCONF", "listening-port", "6379"))
        val result = Replconf().run(protocol)

        assertEquals(Protocol(mutableListOf("OK")), result)
    }
}
