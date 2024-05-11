package config

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.channels.Channel
import org.koin.core.component.KoinComponent
import resp.Protocol

private const val DEFAULT_REDIS_PORT = 6379

public class ReplicaClient(public val reader: ByteReadChannel, public val writer: ByteWriteChannel)

public class Server(args: Array<String>) : KoinComponent {
    public var port: Int = DEFAULT_REDIS_PORT
    public var isSlave: Boolean = false

    public var masterHost: String? = null
    public var masterPort: Int? = null
    public val replicaClients: MutableList<ReplicaClient> = mutableListOf()
    public var lastCommand: Protocol = Protocol(mutableListOf())
    public var propagateResultChannel: Channel<Int> = Channel(Channel.UNLIMITED)

    public lateinit var masterReader: ByteReadChannel
    public lateinit var masterWriter: ByteWriteChannel

    public var replID: String = ""
    public var replOffset: Int = 0

    public var dir: String = ""
    public var dbfilename: String = ""

    init {
        checkOptions(args)
    }

    public fun addReplicaChannel(reader: ByteReadChannel, writer: ByteWriteChannel) {
        replicaClients.add(ReplicaClient(reader, writer))
    }

    private fun checkOptions(args: Array<String>) {
        for (i in args.indices) {
            if (args[i] == "--port" && i + 1 < args.size) {
                args[i + 1].toIntOrNull()?.let {
                    port = it
                }
            }
            if (args[i] == "--replicaof" && i + 2 <= args.size) {
                isSlave = true
                masterHost = args[i + 1]
                args[i + 2].toIntOrNull()?.let {
                    masterPort = it
                }
            }
            if (args[i] == "--dir" && i + 1 <= args.size) {
                dir = args[i + 1]
            }
            if (args[i] == "dbfilename" && i + 1 <= args.size) {
                dbfilename = args[i + 1]
            }
        }

        if (!isSlave) {
            replID = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb"
            replOffset = 0
        }
    }

    public fun getOffset(): Long = masterReader.totalBytesRead

    public fun getReplicaCount(): Int = replicaClients.size

    public fun offsetFromMasterToClient(): Long {
        var count: Long = 0
        replicaClients.forEach {
            count += it.reader.totalBytesRead
        }

        return count
    }
}
