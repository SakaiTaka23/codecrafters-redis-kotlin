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

public class WaitTest : KoinTest {
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
    public fun `returns 0 connection by default`() {
        every { server.getReplicaCount() } returns 0
        val protocol = Protocol(mutableListOf("wait", "0", "60000"))
        val result = Wait().run(protocol)
        assertEquals(Protocol(mutableListOf("0")), result)
    }

    @Test
    public fun `returns correct replica counte when sent different count`() {
        every { server.getReplicaCount() } returns 7
        val protocol = Protocol(mutableListOf("wait", "10", "60000"))
        val result = Wait().run(protocol)
        assertEquals(Protocol(mutableListOf("7")), result)
    }
}
