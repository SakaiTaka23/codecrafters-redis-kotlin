package resp

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

public class EncoderTest : ShouldSpec({
    should("encode array") {
        val protocol = Protocol(mutableListOf("some", "same"))
        val result = protocol.encodeArray()
        result shouldBe mutableListOf("*2\r\n", "$4\r\n", "some\r\n", "$4\r\n", "same\r\n")
    }

    should("return null bulk string on empty bulk string") {
        val protocol = Protocol(mutableListOf("-1"))
        val result = protocol.bulkString()
        result shouldBe mutableListOf("\$-1\r\n")
    }

    should("encode bulk string") {
        val protocol = Protocol(mutableListOf("some"))
        val result = protocol.bulkString()
        result shouldBe mutableListOf("\$4\r\n", "some\r\n")
    }

    should("encode integer") {
        val protocol = Protocol(mutableListOf("100"))
        val result = protocol.integer()
        result shouldBe ":100\r\n"
    }

    should("encode simple string") {
        val protocol = Protocol(mutableListOf("OK"))
        val result = protocol.simpleString()
        result shouldBe "+OK\r\n"
    }

    should("encode simple string with multiple content") {
        val protocol = Protocol(
            mutableListOf(
                "FULLRESYNC",
                "REPL_ID", "0"
            )
        )
        val result = protocol.simpleString()
        result shouldBe "+FULLRESYNC REPL_ID 0\r\n"
    }
})
