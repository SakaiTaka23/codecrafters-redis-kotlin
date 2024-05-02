package commands

import global.RedisCommand
import global.RedisOutput
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import repository.IStorage
import repository.InMemory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val KEY = "key"
private const val VALUE = "value"

public class GetTest : KoinTest {
    private val repo: IStorage by inject()

    @BeforeTest
    public fun beforeTest() {
        startKoin {
            modules(
                module {
                    single<IStorage> { InMemory() }
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

        val command = RedisCommand(3, "get", mutableListOf(KEY))
        val result = Get().run(command)

        assertEquals(RedisOutput(mutableListOf(VALUE)), result)
    }

    @Test
    public fun `returns -1 to non existing value`() {
        val command = RedisCommand(3, "get", mutableListOf(KEY))
        val result = Get().run(command)

        assertEquals(RedisOutput(mutableListOf("-1")), result)
    }
}