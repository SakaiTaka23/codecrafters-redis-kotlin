package repository

public interface StreamStorage {
    public fun set(streamKey: String, timeStamp: String, keyValue: Map<String, String>)
    public fun getKey(streamKey: String): String?
    public fun latestTimeStamp(streamKey: String): String
    public fun getByRange(
        streamKey: String,
        minTime: Int,
        maxTime: Int,
        minSequence: Int = 0,
        maxSequence: Int = Int.MAX_VALUE,
    ): MutableMap<String, Map<String, String>>

    public fun getByStart(
        streamKey: String,
        minTime: Int,
        minSequence: Int,
    ): MutableMap<String, Map<String, String>>
}
