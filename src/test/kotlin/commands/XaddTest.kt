package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import repository.StreamStorage
import resp.Protocol

private const val STREAM_KEY = "stream_key"
private const val TIMESTAMP = "1526919030474-0"

public class XaddTest : ShouldSpec({
    val repo = mockk<StreamStorage>()
    val xadd = Xadd(repo)

    val streamKeyKey: CapturingSlot<String> = slot<String>()
    val timeStampKey: CapturingSlot<String> = slot<String>()
    val keyValueKey: CapturingSlot<Map<String, String>> = slot<Map<String, String>>()

    should("call set with expected value") {
        every { repo.set(capture(streamKeyKey), capture(timeStampKey), capture(keyValueKey)) } returns Unit
        val command = Protocol(
            mutableListOf(
                "XADD",
                STREAM_KEY,
                TIMESTAMP,
                "temperature",
                "36",
                "humidity",
                "95",
            ),
        )
        xadd.run(command)
        streamKeyKey.captured shouldBe STREAM_KEY
        timeStampKey.captured shouldBe TIMESTAMP
        keyValueKey.captured shouldBe mapOf("temperature" to "36", "humidity" to "95")
    }
})
