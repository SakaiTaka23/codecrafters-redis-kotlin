package repository

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
    public fun `can get data on from other instances` () {
        val repo1 = InMemory.getInstance()
        val repo2 = InMemory.getInstance()

        repo1.set("key", "value")
        val result = repo2.get("key")

        assertEquals("value", result)
    }
}
