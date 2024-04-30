package repository

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

public class InMemory private constructor() {
    public companion object {
        @Volatile
        private var instance: InMemory? = null

        public fun getInstance(): InMemory = instance ?: synchronized(this) {
            instance ?: InMemory().also { instance = it }
        }
    }

    private val data = ConcurrentHashMap<String, Pair<String, Instant?>>()

    public fun set(key: String, value: String, expires: Instant? = null) {
        println("setting $key, $value, $expires")
        data[key] = value to expires
    }

    public fun get(key: String): String? {
        val (value, expires) = data[key] ?: return null
        if (expires != null && Instant.now().isAfter(expires)) {
            delete(key)
            return null
        }

        println("was alive $value, $expires")
        return value
    }

    public fun delete(key: String) {
        data.remove(key)
    }
}
