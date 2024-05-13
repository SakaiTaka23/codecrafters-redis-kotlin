package commands

import config.Server
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import replicator.Propagator
import resp.Protocol

public class Wait(
    private val server: Server,
    private val propagator: Propagator
) {
    private val backgroundTasks = Job()
    private val mutex = Mutex()

    public suspend fun run(protocol: Protocol): Protocol {
        val command = server.lastCommand.arguments.getOrNull(0)
        if (command != "set") {
            return Protocol(mutableListOf(server.getReplicaCount().toString()))
        }

        val goalReplica = protocol.arguments[1].toInt()
//        val goalBytes = server.offsetFromMasterToClient() + server.lastWriteCommandByteCount * goalReplica
        val timeLimit = protocol.arguments[2].toLong()

        with(propagator) {
            CoroutineScope(backgroundTasks).sendAck()
        }

        var currentByte = 0
        var got = 0

        mutex.withLock {
            try {
                withTimeout(timeLimit) {
                    while (isActive) {
                        val count = server.propagateResultChannel.receive()
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
        }

        return Protocol(mutableListOf(goalReplica.toString()))
    }
}
