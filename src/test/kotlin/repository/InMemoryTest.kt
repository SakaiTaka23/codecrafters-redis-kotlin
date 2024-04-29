package repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

public class InMemoryTest {
    @Test
    public fun `can set and get data`() {
        val repo = InMemory.getInstance()
        runTest {
            launch {
                repo.set("key", "value")
            }
        }
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

        runTest {
            launch {
                repo.set("key", "value")
            }
        }

        repo.del("key")
        val result = repo.get("key")

        assertEquals("-1", result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    public fun `data gets deleted after expiration`(): TestResult = runTest {
        val repo = InMemory.getInstance()

        repo.set("key", "value", 100)
        val result1 = repo.get("key")
        assertEquals("value", result1, "Before expiration")
        advanceUntilIdle()
        val result2 = repo.get("key")
        assertEquals("-1", result2, "After expiration")
    }
}
