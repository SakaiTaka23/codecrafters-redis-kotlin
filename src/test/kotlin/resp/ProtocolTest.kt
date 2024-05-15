package resp

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

public class ProtocolTest : ShouldSpec({
    should("match bytes") {
        val protocol = Protocol(mutableListOf("set", "foo", "123"))
        val protocolArray = protocol.encodeArray()
        val size = protocolArray.joinToString(separator = "").toByteArray().size
        size shouldBe 31

        val result = protocol.countBytes()
        result shouldBe size
    }
})
