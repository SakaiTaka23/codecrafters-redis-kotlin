package commands

import config.Replica
import config.Server
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import resp.Protocol

public class ReplconfTest : ShouldSpec() {
    private val server = mockk<Server>()
    private val replica = mockk<Replica>()
    private val replconf = Replconf(server)
    private val replconfack = ReplconfAck(replica)

    init {
        coroutineTestScope = true
        context("replconf") {
            should("returns empty protocol on ack") {
                val protocol = Protocol(mutableListOf("replconf", "ACK", "10"))
                coEvery { server.propagateResultChannel.send(10) } returns Unit
                val result = replconf.run(protocol)
                result shouldBe Protocol(mutableListOf())
            }

            should("returns ok") {
                val protocol = Protocol(mutableListOf("replconf"))
                val result = replconf.run(protocol)
                result shouldBe Protocol(mutableListOf("OK"))
            }
        }

        context("replconf ack") {
            should("return ack on request") {
                every { replica.getOffset() } returns 0
                val protocol = Protocol(mutableListOf("replconf", "GETACK"))
                val result = replconfack.run(protocol)
                result shouldBe Protocol(mutableListOf("REPLCONF", "ACK", "0"))
            }
        }
    }
}
