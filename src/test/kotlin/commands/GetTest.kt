package commands

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import repository.InMemory
import resp.Protocol

private const val KEY = "key"
private const val VALUE = "value"

public class GetTest : ShouldSpec({
    isolationMode = IsolationMode.InstancePerLeaf
    val repo = InMemory()
    val get = Get(repo)

    should("return existing value") {
        repo.set(KEY, VALUE)

        val command = Protocol(mutableListOf("get", KEY))
        val result = get.run(command)

        result shouldBe Protocol(mutableListOf(VALUE))
    }

    should("return -1 to non existing value") {
        val command = Protocol(mutableListOf("get", KEY))
        val result = get.run(command)

        result shouldBe Protocol(mutableListOf("-1"))
    }
})
