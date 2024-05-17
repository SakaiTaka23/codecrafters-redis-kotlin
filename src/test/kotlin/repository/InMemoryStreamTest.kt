package repository

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

private const val STREAM_KEY = "stream_key"

public class InMemoryStreamTest : ShouldSpec({
    val srepo = InMemoryStream()

    should("returns stored keys") {
        srepo.set(STREAM_KEY, "0-1", mapOf("apple" to "orange"))
        val key = srepo.getKey(STREAM_KEY)
        key shouldBe STREAM_KEY
    }
})
