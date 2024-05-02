package commands

import config.Server
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import resp.Protocol
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

public class InfoTest : KoinTest {
    @AfterTest
    public fun afterTest() {
        stopKoin()
    }

    @Test
    public fun `act as master replication when no args are passed`() {
        val args = arrayOf<String>()
        startKoin {
            modules(
                module {
                    single<Server> { Server(args) }
                }
            )
        }

        val command = Protocol(mutableListOf("info", "replication"))
        val result = Info().run(command)

        assertEquals(
            Protocol(
                mutableListOf(
                    "role:master\r\n" +
                            "master_replid:8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb\r\n" +
                            "master_repl_offset:0"
                )
            ),
            result
        )
    }

    @Test
    public fun `information of master replication`() {
        val args = arrayOf("--port", "6380")
        startKoin {
            modules(
                module {
                    single<Server> { Server(args) }
                }
            )
        }

        val command = Protocol(mutableListOf("info", "replication"))
        val result = Info().run(command)

        assertEquals(
            Protocol(
                mutableListOf(
                    "role:master\r\n" +
                            "master_replid:8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb\r\n" +
                            "master_repl_offset:0"
                )
            ),
            result
        )
    }

    @Test
    public fun `returns role slave when it is replica`() {
        val args = arrayOf("--port", "6380", "--replicaof", "localhost", "6379")
        startKoin {
            modules(
                module {
                    single<Server> { Server(args) }
                }
            )
        }

        val command = Protocol(mutableListOf("info", "replication"))
        val result = Info().run(command)

        assertEquals(
            Protocol(mutableListOf("role:slave")),
            result
        )
    }

    @Test
    public fun `returns empty string on invalid section`() {
        val args = arrayOf("--port", "6380")
        startKoin {
            modules(
                module {
                    single<Server> { Server(args) }
                }
            )
        }

        val command = Protocol(mutableListOf("info", ""))
        val result = Info().run(command)

        assertEquals(
            Protocol(mutableListOf()),
            result
        )
    }
}
