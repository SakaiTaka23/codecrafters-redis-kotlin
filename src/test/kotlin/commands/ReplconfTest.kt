package commands

import resp.Protocol
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

public class ReplconfTest {
    @Test
    public fun `returns ok`() {
        val protocol = Protocol(mutableListOf("REPLCONF", "listening-port", "6379"))
        val result = Replconf().run(protocol)

        assertEquals(Protocol(mutableListOf("OK")), result)
    }
}

public class ReplconfAckTest {
    @Test
    public fun `returns ack on request`() {
        val protocol = Protocol(mutableListOf("REPLCONF", "GETACK", "*"))
        val result = ReplconfAck().run(protocol)

        assertEquals(Protocol(mutableListOf("REPLCONF", "ACK", "0")), result)
    }

    @Test
    public fun `returns error on invalid command`() {
        val protocol = Protocol(mutableListOf("REPLCONF", "GETAC", "some"))
        val replconfAck = ReplconfAck()

        val exception = assertFailsWith<IllegalStateException> {
            replconfAck.run(protocol)
        }

        assertEquals("invalid command", exception.message)
    }
}
