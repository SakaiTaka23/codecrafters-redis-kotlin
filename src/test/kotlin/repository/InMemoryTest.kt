package repository

import java.time.Instant
import java.util.concurrent.TimeUnit
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val KEY = "key"
private const val VALUE = "value"

public class InMemoryTest : KoinTest {
    private val repo: Storage by inject()
    private val repo2: Storage by inject()

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
    public fun `can set and get data`() {
        repo.set(KEY, VALUE)

        val result = repo.get(KEY)

        assertEquals(VALUE, result)
    }

    @Test
    public fun `can get data on from other instances`() {
        repo.set(KEY, VALUE)
        val result = repo2.get(KEY)

        assertEquals(VALUE, result)
    }

    @Test
    public fun `can get deleted manually`() {
        repo.set(KEY, VALUE)
        repo.delete(KEY)
        val result = repo.get(KEY)

        assertEquals(null, result)
    }

    @Test
    public fun `data gets stored and get deleted after expiration`() {
        repo.set(KEY, VALUE, Instant.now().plusMillis(100))
        val result1 = repo.get(KEY)
        assertEquals(VALUE, result1)

        TimeUnit.MILLISECONDS.sleep(101)
        val result2 = repo.get(KEY)
        assertEquals(null, result2)
    }
}
