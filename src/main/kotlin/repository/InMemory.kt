package repository

import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

public class InMemory private constructor() {
    public companion object {
        @Volatile
        private var instance: InMemory? = null

        public fun getInstance(): InMemory = instance ?: synchronized(this) {
            instance ?: InMemory().also { instance = it }
        }
    }

    private val data = ConcurrentHashMap<String, String>()

    public suspend fun set(key: String, value: String, expires: Long = -1): Unit = coroutineScope {
        data[key] = value
        launch {
            if (expires > 0) {
                delay(expires)
                del(key)
            }
        }
    }

    public fun get(key: String): String = data[key] ?: "-1"

    public fun del(key: String) {
//        data.remove(key)
        data[key] = "expired"
    }
}
