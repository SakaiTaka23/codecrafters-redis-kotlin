package commands

import config.Server
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import resp.Protocol

private const val DIR = "/tmp/dir"
private const val DB_FILENAME = "apple.db"

public class ConfigTest : ShouldSpec({
    val server = mockk<Server>()
    val config = Config(server)

    should("return dir and dbname") {
        val protocol = Protocol(mutableListOf("config", "dir", "dbname"))
        every { server.dir } returns DIR
        every { server.dbfilename } returns DB_FILENAME
        val result = config.run(protocol)

        result shouldBe Protocol(
            mutableListOf(
                "dir", DIR,
                "dbname", DB_FILENAME
            )
        )
    }

    should("return empty protocol when no argument") {
        val protocol = Protocol(mutableListOf("config"))
        every { server.dir } returns DIR
        every { server.dbfilename } returns DB_FILENAME
        val result = config.run(protocol)

        result shouldBe Protocol(mutableListOf())
    }
})
