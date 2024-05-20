package repository

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

private const val STREAM_KEY = "stream_key"

public class InMemoryStreamTest : ShouldSpec({
    val srepo = InMemoryStream()

    should("return stored keys") {
        srepo.set(STREAM_KEY, "0-1", mapOf("apple" to "orange"))
        val key = srepo.getKey(STREAM_KEY)
        key shouldBe STREAM_KEY
    }

    should("return latest timestamp") {
        srepo.set(STREAM_KEY, "0-1", mapOf("apple" to "orange"))
        srepo.set(STREAM_KEY, "1-1", mapOf("apple" to "pineapple"))

        val result = srepo.latestTimeStamp(STREAM_KEY)

        result shouldBe "1-1"
    }

    should("return ranges without sequence") {
        val mockData = mapOf("apple" to "orange")
        srepo.set(STREAM_KEY, "0-1", mockData)
        srepo.set(STREAM_KEY, "10-0", mockData)
        srepo.set(STREAM_KEY, "10-1", mockData)
        srepo.set(STREAM_KEY, "12-0", mockData)
        srepo.set(STREAM_KEY, "50-0", mockData)

        val result = srepo.getByRange(STREAM_KEY, 9, 11)

        result shouldBe mutableMapOf(
            "10-0" to mockData,
            "10-1" to mockData,
        )
    }

    should("return ranges with sequence") {
        val mockData = mapOf("apple" to "orange")
        srepo.set(STREAM_KEY, "0-1", mockData)
        srepo.set(STREAM_KEY, "10-0", mockData)
        srepo.set(STREAM_KEY, "10-1", mockData)
        srepo.set(STREAM_KEY, "10-2", mockData)
        srepo.set(STREAM_KEY, "10-3", mockData)
        srepo.set(STREAM_KEY, "12-0", mockData)
        srepo.set(STREAM_KEY, "50-0", mockData)

        val result = srepo.getByRange(STREAM_KEY, 10, 10, 1, 2)

        result shouldBe mutableMapOf(
            "10-1" to mockData,
            "10-2" to mockData,
        )
    }
})
