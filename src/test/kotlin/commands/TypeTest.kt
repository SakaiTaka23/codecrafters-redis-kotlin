package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import repository.InMemory
import repository.InMemoryStream
import resp.Protocol

private const val KEY = "key"
private const val VALUE = "value"

public class TypeTest : ShouldSpec({
    val repo = mockk<InMemory>()
    val srepo = mockk<InMemoryStream>()
    val type = Type(repo, srepo)

    should("return none when key is not found") {
        every { repo.get(KEY) } returns null
        every { srepo.getKey(KEY) } returns null
        val protocol = Protocol(mutableListOf("type", KEY))
        val result = type.run(protocol)
        result shouldBe Protocol(mutableListOf("none"))
    }

    should("return string when key was found as key value") {
        every { repo.get(KEY) } returns VALUE
        val protocol = Protocol(mutableListOf("type", KEY))
        val result = type.run(protocol)
        result shouldBe Protocol(mutableListOf("string"))
    }

    should("return stream when key was found as stream") {
        every { repo.get(KEY) } returns null
        every { srepo.getKey(KEY) } returns VALUE
        val protocol = Protocol(mutableListOf("type", KEY))
        val result = type.run(protocol)
        result shouldBe Protocol(mutableListOf("stream"))
    }
})
