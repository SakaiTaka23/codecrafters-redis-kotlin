package commands

import config.Server
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import resp.Protocol
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

public class PsyncTest : KoinTest {
    private val server: Server by inject()

    @BeforeTest
    public fun beforeTest() {
        startKoin {
            modules(
                module {
                    single<Server> { Server(arrayOf("--port", "6380", "--replicaof", "localhost", "6379")) }
                }
            )
        }
    }

    @AfterTest
    public fun afterTest() {
        stopKoin()
    }

    @Test
    public fun `returns full resync`() {
        server.replID = "mock_id"
        server.replOffset = 0
        val protocol = Protocol(mutableListOf("psync", "?", "-1"))
        val result = Psync().run(protocol)

        assertEquals(
            Protocol(mutableListOf("FULLRESYNC", "mock_id", "0")), result
        )
    }
}
