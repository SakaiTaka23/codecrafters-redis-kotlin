package repository

import java.util.concurrent.ConcurrentHashMap

public data class StreamData(
    val timeStamp: String,
    val data: Map<String, String>,
)

public class InMemoryStream : StreamStorage {
    private val data = ConcurrentHashMap<String, MutableMap<String, Map<String, String>>>()

    public override fun set(streamKey: String, timeStamp: String, keyValue: Map<String, String>) {
        data[streamKey] = mutableMapOf(timeStamp to keyValue)
    }

    public override fun getKey(streamKey: String): String? {
        val stream = data[streamKey]
        return if (stream != null) {
            streamKey
        } else {
            null
        }
    }
}
