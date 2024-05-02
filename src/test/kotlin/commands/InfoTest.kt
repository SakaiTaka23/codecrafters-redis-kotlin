package commands

import config.Server
import global.RedisCommand
import global.RedisOutput
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

public class InfoTest : KoinTest {
    @AfterTest
    public fun afterTest() {
        stopKoin()
    }

    @Test
    public fun `information of replication`() {
        val args = arrayOf("--port", "6380")
        startKoin {
            modules(
                module {
                    single<Server> { Server(args) }
                }
            )
        }

        val command = RedisCommand(1, "info", mutableListOf("replication"))
        val result = Info().run(command)

        assertEquals(
            RedisOutput(mutableListOf("role:master")),
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

        val command = RedisCommand(1, "info", mutableListOf("replication"))
        val result = Info().run(command)

        assertEquals(
            RedisOutput(mutableListOf("role:slave")),
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

        val command = RedisCommand(1, "info", mutableListOf(""))
        val result = Info().run(command)

        assertEquals(
            RedisOutput(mutableListOf()),
            result
        )
    }
}
