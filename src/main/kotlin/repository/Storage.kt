package repository

import java.time.Instant

public interface Storage {
    public fun set(key: String, value: String, expires: Instant? = null)
    public fun get(key: String): String?
    public fun delete(key: String)
}
