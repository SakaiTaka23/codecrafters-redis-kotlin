package repository

public data class StreamData(
    val timeStamp: String,
    val data: Map<String, String>,
)

public class InMemoryStream : StreamStorage {
    private val data: MutableMap<String, MutableMap<String, Map<String, String>>> = LinkedHashMap()

    public override fun set(streamKey: String, timeStamp: String, keyValue: Map<String, String>) {
        val stream = data.getOrPut(streamKey) { LinkedHashMap() }
        stream[timeStamp] = keyValue
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

    override fun getByRange(
        streamKey: String,
        minTime: Int,
        maxTime: Int,
        minSequence: Int,
        maxSequence: Int,
    ): MutableMap<String, Map<String, String>> {
        val result = mutableMapOf<String, Map<String, String>>()
        val stream = data[streamKey] ?: return result
        stream.forEach { data ->
            val rawTimeStamp = data.key.splitTimeStamp()
            val timeStamp = rawTimeStamp[0].toInt()
            val sequence = rawTimeStamp[1].toInt()
            if (timeStamp in minTime..maxTime) {
                if (timeStamp > minTime || timeStamp < maxTime) {
                    result[data.key] = data.value
                } else if (sequence in minSequence..maxSequence) {
                    result[data.key] = data.value
                }
            }
        }

        return result
    }
}

public fun String.splitTimeStamp(): List<String> {
    return if (this == "-") {
        listOf("-")
    } else {
        this.split("-")
    }
}
