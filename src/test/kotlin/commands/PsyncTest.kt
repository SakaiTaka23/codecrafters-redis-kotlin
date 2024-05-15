package commands

import config.Server
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import resp.Protocol

private const val REPL_ID = "12345"
private const val REPL_OFFSET = 10

public class PsyncTest : ShouldSpec({
    val server = mockk<Server>()
    val psync = Psync(server)

    should("return fullresync on handshake") {
        every { server.replID } returns REPL_ID
        every { server.replOffset } returns REPL_OFFSET
        val protocol = Protocol(mutableListOf("psync", "?", "-1"))
        val result = psync.run(protocol)
        result shouldBe Protocol(mutableListOf("FULLRESYNC", REPL_ID, REPL_OFFSET.toString()))
    }
})
