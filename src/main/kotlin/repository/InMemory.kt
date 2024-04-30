package repository

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

public class InMemory : IStorage {
    private val data = ConcurrentHashMap<String, Pair<String, Instant?>>()

    public override fun set(key: String, value: String, expires: Instant?) {
        println("setting $key, $value, $expires")
        data[key] = value to expires
    }

    public override fun get(key: String): String? {
        val (value, expires) = data[key] ?: return null
        if (expires != null && Instant.now().isAfter(expires)) {
            delete(key)
            return null
        }

        println("was alive $value, $expires")
        return value
    }

    public override fun delete(key: String) {
        data.remove(key)
    }
}
