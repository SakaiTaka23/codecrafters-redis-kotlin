package commands

import config.Server
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import replicator.Propagator
import resp.Protocol

public class Wait : KoinComponent {
    private val server: Server by inject()
    private val propagator: Propagator by inject()

    public suspend fun run(protocol: Protocol): Protocol {
        val command = server.lastCommand.arguments.getOrNull(0)
        if (command != "set") {
            return Protocol(mutableListOf(server.getReplicaCount().toString()))
        }

        val goalReplica = protocol.arguments[1].toInt()
//        val goalBytes = server.offsetFromMasterToClient() + server.lastWriteCommandByteCount * goalReplica
        val timeLimit = protocol.arguments[2].toLong()
        server.propagateResultChannel = Channel(Channel.UNLIMITED)

        propagator.sendAck()

        var currentByte = 0
        var got = 0

        try {
            withTimeout(timeLimit) {
                while (isActive) {
                    val count = server.propagateResultChannel.receive()
                    println("got it!")
                    got += 1
                    currentByte += count
                    if (got >= goalReplica) {
                        return@withTimeout
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            return Protocol(mutableListOf(got.toString()))
        }

        return Protocol(mutableListOf(goalReplica.toString()))
    }
}
