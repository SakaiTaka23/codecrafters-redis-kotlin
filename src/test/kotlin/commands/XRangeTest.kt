package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import repository.InMemoryStream
import resp.Entry
import resp.Protocol

private const val STREAM_KEY = "key"

public class XRangeTest : ShouldSpec({
    val repo = mockk<InMemoryStream>()
    val xRange = XRange(repo)

    val timeArg: CapturingSlot<Int> = slot<Int>()

    should("return value in expected format") {
        val mockMap = mapOf("apple" to "orange")
        every { repo.getByRange(STREAM_KEY, 1, 10) } returns mutableMapOf(
            "10-1" to mockMap,
            "10-2" to mockMap,
        )
        val protocol = Protocol(mutableListOf("xrange", STREAM_KEY, "1", "10"))

        val result = xRange.run(protocol)

        result shouldBe listOf(
            Entry("10-1", listOf("apple", "orange")),
            Entry("10-2", listOf("apple", "orange")),
        )
    }

    should("call range with 0 when - is minimum") {
        every { repo.getByRange(STREAM_KEY, capture(timeArg), 10) } returns mutableMapOf()
        val protocol = Protocol(mutableListOf("xrange", STREAM_KEY, "-", "10"))
        xRange.run(protocol)
        timeArg.captured shouldBe 0
    }

    should("call range with Int.MAX_VALUE when + is maximum") {
        every { repo.getByRange(STREAM_KEY, 0, capture(timeArg)) } returns mutableMapOf()
        val protocol = Protocol(mutableListOf("xrange", STREAM_KEY, "0", "+"))
        xRange.run(protocol)
        timeArg.captured shouldBe Int.MAX_VALUE
    }
})
