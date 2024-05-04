package resp

import kotlin.test.Test
import kotlin.test.assertEquals

public class EncoderTest {
    @Test
    public fun `can encode array`() {
        val protocol = Protocol(mutableListOf("some", "same"))
        val result = protocol.encodeArray()
        assertEquals(
            mutableListOf("*2\r\n", "$4\r\n", "some\r\n", "$4\r\n", "same\r\n"),
            result
        )
    }

    @Test
    public fun `returns null bulk string on empty bulk string`() {
        val protocol = Protocol(mutableListOf("-1"))
        val result = protocol.bulkString()
        assertEquals(mutableListOf("\$-1\r\n"), result)
    }

    @Test
    public fun `can encode bulk string`() {
        val protocol = Protocol(mutableListOf("some"))
        val result = protocol.bulkString()
        assertEquals(mutableListOf("\$4\r\n", "some\r\n"), result)
    }

    @Test
    public fun `can encode integer`() {
        val protocol = Protocol(mutableListOf("100"))
        val result = protocol.integer()
        assertEquals(":100\r\n", result)
    }

    @Test
    public fun `can encode simple string`() {
        val protocol = Protocol(mutableListOf("OK"))
        val result = protocol.simpleString()
        assertEquals("+OK\r\n", result)
    }

    @Test
    public fun `can encode simple string with multiple content`() {
        val protocol = Protocol(
            mutableListOf(
                "FULLRESYNC",
                "REPL_ID", "0"
            )
        )
        val result = protocol.simpleString()
        assertEquals(
            "+FULLRESYNC REPL_ID 0\r\n",
            result
        )
    }
}
