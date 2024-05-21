package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import repository.InMemoryStream
import resp.Entry
import resp.Protocol
import resp.StreamEntry

private const val STREAM_KEY = "stream_key"

public class XReadTest : ShouldSpec({
    val repo = mockk<InMemoryStream>()
    val xread = XRead(repo)
    val mockMap = mapOf("apple" to "orange")

    should("return data in expected format") {
        every { repo.getByStart(STREAM_KEY, 10, 0) } returns mutableMapOf(
            "11-0" to mockMap,
            "12-0" to mockMap,
        )

        val protocol = Protocol(mutableListOf("xread", "streams", STREAM_KEY, "10-0"))
        val result = xread.run(protocol)

        result shouldBe listOf(
            StreamEntry(
                STREAM_KEY,
                listOf(
                    Entry("11-0", listOf("apple", "orange")),
                    Entry("12-0", listOf("apple", "orange")),
                ),
            ),
        )
    }
})
