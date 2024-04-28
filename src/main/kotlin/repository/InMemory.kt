package repository

import java.util.concurrent.ConcurrentHashMap

public class InMemory {
    private val data = ConcurrentHashMap<String, String>()

    public fun set(key: String, value: String) {
        data[key] = value
    }

    public fun get(key: String): String = data[key] ?: "-1"
}
