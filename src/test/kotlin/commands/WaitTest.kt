package commands

import resp.Protocol
import kotlin.test.Test
import kotlin.test.assertEquals

public class WaitTest {
    @Test
    public fun `returns 0 on request`() {
        val protocol = Protocol(mutableListOf("wait", "0", "60000"))
        val result = Wait().run(protocol)
        assertEquals(Protocol(mutableListOf("0")), result)
    }
}
