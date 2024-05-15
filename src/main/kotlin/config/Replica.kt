package config

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel

public class Replica(
    // Replica Node Only
    public val reader: ByteReadChannel,
    public val writer: ByteWriteChannel,
) {
    public fun getOffset(): Long = reader.totalBytesRead
}
