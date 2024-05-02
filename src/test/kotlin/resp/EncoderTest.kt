package resp

import kotlin.test.Test
import kotlin.test.assertEquals

public class EncoderTest {
    @Test
    public fun `can encode array`() {
        val protocol = Protocol(mutableListOf("some", "same"))
        val result = protocol.encodeArray()
        assertEquals(
            "*2\r\n$4\r\nsome\r\n$4\r\nsame\r\n",
            result
        )
    }

    @Test
    public fun `returns null bulk string on empty bulk string`() {
        val protocol = Protocol(mutableListOf("-1"))
        val result = protocol.bulkString()
        assertEquals("\$-1\r\n", result)
    }

    @Test
    public fun `can encode bulk string`() {
        val protocol = Protocol(mutableListOf("some"))
        val result = protocol.bulkString()
        assertEquals("\$4\r\nsome\r\n", result)
    }

    @Test
    public fun `can encode simple string`() {
        val protocol = Protocol(mutableListOf("OK"))
        val result = protocol.simpleString()
        assertEquals("+OK\r\n", result)
    }
}
