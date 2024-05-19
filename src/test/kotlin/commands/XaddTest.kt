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
private const val TIMESTAMP = "0-2"

public class XaddTest : ShouldSpec({
    val repo = mockk<StreamStorage>()
    val xadd = Xadd(repo)

    val streamKeyKey: CapturingSlot<String> = slot<String>()
    val timeStampKey: CapturingSlot<String> = slot<String>()
    val keyValueKey: CapturingSlot<Map<String, String>> = slot<Map<String, String>>()

    should("call set with expected value") {
        every { repo.set(capture(streamKeyKey), capture(timeStampKey), capture(keyValueKey)) } returns Unit
        every { repo.latestTimeStamp(STREAM_KEY) } returns "0-1"
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
        val result = xadd.run(command)
        result shouldBe Protocol(mutableListOf(TIMESTAMP))
        streamKeyKey.captured shouldBe STREAM_KEY
        timeStampKey.captured shouldBe TIMESTAMP
        keyValueKey.captured shouldBe mapOf("temperature" to "36", "humidity" to "95")
    }

    should("return parameter error on 0-0 timestamp") {
        val command = Protocol(
            mutableListOf(
                "XADD",
                STREAM_KEY,
                "0-0",
                "temperature",
                "36",
                "humidity",
                "95",
            ),
        )
        val result = xadd.run(command)
        result shouldBe Protocol(
            mutableListOf("ERR The ID specified in XADD must be greater than 0-0"),
        )
    }

    should("return error on validation fail") {
        every { repo.set(any(), any(), any()) } returns Unit
        every { repo.latestTimeStamp(STREAM_KEY) } returns "99-1"
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
        val result = xadd.run(command)
        result shouldBe Protocol(
            mutableListOf("ERR The ID specified in XADD is equal or smaller than the target stream top item"),
        )
    }
})
