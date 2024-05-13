package commands

import config.Server
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import resp.Protocol

private const val REPL_ID = "12345"
private const val REPL_OFFSET = 10

public class InfoTest : ShouldSpec({
    val server = mockk<Server>()
    val info = Info(server)

    should("response when replication option and slave") {
        every { server.isSlave } returns true
        val protocol = Protocol(mutableListOf("info", "replication"))
        val result = info.run(protocol)
        result shouldBe Protocol(mutableListOf("role:slave"))
    }

    should("response when replication option and master") {
        every { server.isSlave } returns false
        every { server.replID } returns REPL_ID
        every { server.replOffset } returns REPL_OFFSET
        val protocol = Protocol(mutableListOf("info", "replication"))
        val result = info.run(protocol)
        result shouldBe Protocol(
            mutableListOf(
                "role:master\r\n"
                        + "master_replid:${REPL_ID}\r\n"
                        + "master_repl_offset:${REPL_OFFSET}"
            )
        )
    }
})
