package repository

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

public class InMemory : Storage {
    private val data = ConcurrentHashMap<String, Pair<String, Instant?>>()

    public override fun set(key: String, value: String, expires: Instant?) {
        data[key] = value to expires
    }

    public override fun get(key: String): String? {
        val (value, expires) = data[key] ?: return null
        if (expires != null && Instant.now().isAfter(expires)) {
            delete(key)
            return null
        }

        return value
    }

    override fun getAllKey(): MutableList<String> = data.keys().toList().toMutableList()

    public override fun delete(key: String) {
        data.remove(key)
    }
}
