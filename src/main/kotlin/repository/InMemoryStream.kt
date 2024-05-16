package repository

import java.util.concurrent.ConcurrentHashMap

public data class StreamData(
    val timeStamp: String,
    val data: Map<String, String>,
)

public class InMemoryStream : StreamStorage {
    private val data = ConcurrentHashMap<String, MutableMap<String, Map<String, String>>>()

    public override fun set(streamKey: String, timeStamp: String, keyValue: Map<String, String>) {
        val stream = data[streamKey]
        keyValue.forEach {
            stream?.put(timeStamp, mapOf(it.key to it.value))
        }
    }
}
