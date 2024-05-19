package repository

public data class StreamData(
    val timeStamp: String,
    val data: Map<String, String>,
)

public class InMemoryStream : StreamStorage {
    private val data: MutableMap<String, MutableMap<String, Map<String, String>>> = LinkedHashMap()

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

    public override fun latestTimeStamp(streamKey: String): String {
        val stream = data[streamKey]
        return stream?.entries?.last()?.key ?: "0-0"
    }
}
