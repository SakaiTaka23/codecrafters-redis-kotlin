package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import resp.Protocol

private const val ECHO_STR = "apple"

public class EchoTest : ShouldSpec({
    should("return expected words") {
        val input = Protocol(mutableListOf("echo", ECHO_STR))
        val result = Echo().run(input)

        result shouldBe Protocol(mutableListOf(ECHO_STR))
    }
})
