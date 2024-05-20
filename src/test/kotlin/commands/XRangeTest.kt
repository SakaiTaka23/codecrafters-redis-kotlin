package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import repository.InMemoryStream
import resp.Entry
import resp.Protocol

private const val STREAM_KEY = "key"

public class XRangeTest : ShouldSpec({
    val repo = mockk<InMemoryStream>()
    val xRange = XRange(repo)

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
})
