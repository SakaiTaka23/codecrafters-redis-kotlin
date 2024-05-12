package config

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel

public class Replica private constructor(
    // Replica Node Only
    public val reader: ByteReadChannel,
    public val writer: ByteWriteChannel,
) {
    public companion object {
        private var instance: Replica? = null

        public fun getInstance(
            reader: ByteReadChannel,
            writer: ByteWriteChannel,
        ): Replica = instance ?: Replica(
            reader,
            writer,
        ).also { instance = it }

        public fun getInstance(): Replica = instance ?: throw IllegalStateException("Instance has not been initialized")
    }

    public fun getOffset(): Long = reader.totalBytesRead
}
