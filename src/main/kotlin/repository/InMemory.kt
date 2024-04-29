package repository

import java.util.concurrent.ConcurrentHashMap

public class InMemory private constructor() {
    public companion object {
        @Volatile
        private var instance: InMemory? = null

        public fun getInstance(): InMemory = instance ?: synchronized(this) {
            instance ?: InMemory().also { instance = it }
        }
    }

    private val data = ConcurrentHashMap<String, String>()

    public fun set(key: String, value: String) {
        data[key] = value
    }

    public fun get(key: String): String = data[key] ?: "-1"
}
