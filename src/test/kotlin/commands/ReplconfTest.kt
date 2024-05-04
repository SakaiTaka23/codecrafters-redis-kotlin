package commands

import config.Server
import io.mockk.every
import io.mockk.mockk
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import resp.Protocol
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

public class ReplconfTest {
    @Test
    public fun `returns ok`() {
        val protocol = Protocol(mutableListOf("REPLCONF", "listening-port", "6379"))
        val result = Replconf().run(protocol)

        assertEquals(Protocol(mutableListOf("OK")), result)
    }
}

public class ReplconfAckTest : KoinTest {
    private val server: Server = mockk<Server>()

    @BeforeTest
    public fun beforeTest() {
        startKoin {
            modules(
                module {
                    single<Server> { server }
                }
            )
        }
    }

    @AfterTest
    public fun afterTest() {
        stopKoin()
    }

    @Test
    public fun `returns ack 0 on default request`() {
        every { server.getOffset() } returns 0
        val protocol = Protocol(mutableListOf("REPLCONF", "GETACK", "*"))
        val result = ReplconfAck().run(protocol)

        assertEquals(Protocol(mutableListOf("REPLCONF", "ACK", "0")), result)
    }

    @Test
    public fun `returns error on invalid command`() {
        val protocol = Protocol(mutableListOf("REPLCONF", "GETAC", "some"))
        val replconfAck = ReplconfAck()

        val exception = assertFailsWith<IllegalStateException> {
            replconfAck.run(protocol)
        }

        assertEquals("invalid command", exception.message)
    }
}
