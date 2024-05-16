package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import repository.InMemory
import resp.Protocol

private const val KEY = "key"
private const val VALUE = "value"

public class TypeTest : ShouldSpec({
    val repo = mockk<InMemory>()
    val type = Type(repo)

    should("return none when key is not found") {
        every { repo.get(KEY) } returns null
        val protocol = Protocol(mutableListOf("type", KEY))
        val result = type.run(protocol)
        result shouldBe Protocol(mutableListOf("none"))
    }

    should("return string when key was found") {
        every { repo.get(KEY) } returns VALUE
        val protocol = Protocol(mutableListOf("type", KEY))
        val result = type.run(protocol)
        result shouldBe Protocol(mutableListOf("string"))
    }
})
