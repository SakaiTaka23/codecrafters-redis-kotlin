package repository

import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

public class InMemoryTest {
    @Test
    public fun `can set and get data`() {
        val repo = InMemory.getInstance()

        repo.set("key", "value")

        val result = repo.get("key")

        assertEquals("value", result)
    }

    @Test
    public fun `can get data on from other instances`() {
        val repo1 = InMemory.getInstance()
        val repo2 = InMemory.getInstance()

        runTest {
            launch {
                repo1.set("key", "value")
            }
        }
        val result = repo2.get("key")

        assertEquals("value", result)
    }

    @Test
    public fun `can get deleted manually`() {
        val repo = InMemory.getInstance()

        repo.set("key", "value")
        repo.delete("key")
        val result = repo.get("key")

        assertEquals(null, result)
    }

    @Test
    public fun `data gets stored and get deleted after expiration`() {
        val repo = InMemory.getInstance()

        repo.set("key", "value", Instant.now().plusMillis(100))
        val result1 = repo.get("key")
        assertEquals("value", result1)

        TimeUnit.MILLISECONDS.sleep(101)
        val result2 = repo.get("key")
        assertEquals(null, result2)
    }
}
