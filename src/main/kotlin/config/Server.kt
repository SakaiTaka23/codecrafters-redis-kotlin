package config

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.channels.Channel
import resp.Protocol

public class ReplicaClient(public val reader: ByteReadChannel, public val writer: ByteWriteChannel)

@Suppress("LongParameterList")
public class Server (
    // Common Properties
    public val port: Int,
    public val isSlave: Boolean,
    public val replID: String,
    public val replOffset: Int,
    public val dir: String,
    public val dbfilename: String,
    public var lastCommand: Protocol,

    // Master Node Only
    public val propagateResultChannel: Channel<Int>,
    public val replicaClients: MutableList<ReplicaClient>,
) {
    public fun addReplicaChannel(reader: ByteReadChannel, writer: ByteWriteChannel) {
        replicaClients.add(ReplicaClient(reader, writer))
    }

    public fun getReplicaCount(): Int = replicaClients.size

    public fun offsetFromMasterToClient(): Long {
        var count: Long = 0
        replicaClients.forEach {
            count += it.reader.totalBytesRead
        }

        return count
    }
}
