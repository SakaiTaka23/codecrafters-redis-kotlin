package commands

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import repository.InMemory
import repository.Storage
import resp.Protocol
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val KEY = "key"
private const val VALUE = "value"

public class GetTest : KoinTest {
    private val repo: Storage by inject()

    @BeforeTest
    public fun beforeTest() {
        startKoin {
            modules(
                module {
                    single<Storage> { InMemory() }
                }
            )
        }
    }

    @AfterTest
    public fun afterTest() {
        stopKoin()
    }

    @Test
    public fun `can return existing value`() {
        repo.set(KEY, VALUE)

        val command = Protocol(mutableListOf("get", KEY))
        val result = Get().run(command)

        assertEquals(Protocol(mutableListOf(VALUE)), result)
    }

    @Test
    public fun `returns -1 to non existing value`() {
        val command = Protocol(mutableListOf("get", KEY))
        val result = Get().run(command)

        assertEquals(Protocol(mutableListOf("-1")), result)
    }
}
