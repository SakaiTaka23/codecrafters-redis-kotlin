package repository

public interface StreamStorage {
    public fun set(streamKey: String, timeStamp: String, keyValue: Map<String, String>)
}
