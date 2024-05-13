package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import repository.Storage
import resp.Protocol


public class KesTest : ShouldSpec({
    val storedKeys = mutableListOf("apple", "orange")
    val storage = mockk<Storage>()
    val keys = Keys(storage)

    should("return keys") {
        every { storage.getAllKey() } returns storedKeys
        val protocol = Protocol(mutableListOf("keys"))
        val result = keys.run(protocol)
        result shouldBe Protocol(storedKeys)
    }
})
