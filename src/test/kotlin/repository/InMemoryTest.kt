package repository

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.concurrent.TimeUnit

private const val KEY = "key"
private const val VALUE = "value"

public class InMemoryTest : ShouldSpec({
    val repo = InMemory()

    should("set and get data") {
        repo.set(KEY, VALUE)
        val result = repo.get(KEY)

        result shouldBe VALUE
    }

    should("be deleted after expiration") {
        repo.set(KEY, VALUE, Instant.now().plusMillis(100))
        val result1 = repo.get(KEY)
        result1 shouldBe VALUE

        TimeUnit.MILLISECONDS.sleep(101)
        val result2 = repo.get(KEY)
        result2 shouldBe null
    }
})
