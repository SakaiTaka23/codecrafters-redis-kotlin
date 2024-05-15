package commands

import config.Server
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import replicator.Propagator
import resp.Protocol

public class WaitTest : ShouldSpec() {
    private val server = mockk<Server>()
    private val propagator = mockk<Propagator>(relaxed = true)
    private val wait = Wait(server, propagator)

    init {
        coroutineTestScope = true

        should("return replica count if last command is not write command") {
            every { server.lastCommand.arguments } returns mutableListOf("get", "key")
            every { server.getReplicaCount() } returns 10
            val protocol = Protocol(mutableListOf("wait", "5", "1000"))
            val result = wait.run(protocol)

            result shouldBe Protocol(mutableListOf("10"))
        }

        should("return on goal") {
            val protocol = Protocol(mutableListOf("wait", "5", "1000"))

            every { server.lastCommand.arguments } returns mutableListOf("set", "key", "value")
            coEvery { server.propagateResultChannel.receive() } returns 10

            val result = wait.run(protocol)
            result shouldBe Protocol(mutableListOf("5"))
        }
    }
}
