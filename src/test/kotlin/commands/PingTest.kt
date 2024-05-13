package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import resp.Protocol

public class PingTest : ShouldSpec({
    val ping = Ping()

    should("return pong") {
        val protocol = Protocol(mutableListOf("ping"))
        val result = ping.run(protocol)
        result shouldBe Protocol(mutableListOf("PONG"))
    }
})
