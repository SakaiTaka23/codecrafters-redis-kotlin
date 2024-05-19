package repository

public interface StreamStorage {
    public fun set(streamKey: String, timeStamp: String, keyValue: Map<String, String>)
    public fun getKey(streamKey: String): String?
    public fun latestTimeStamp(streamKey: String): String
}
