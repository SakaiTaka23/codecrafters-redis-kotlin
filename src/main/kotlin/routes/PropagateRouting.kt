package routes

import config.Replica
import io.ktor.utils.io.ByteWriteChannel
import java.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import presentor.Responder
import reciever.Reader
import repository.Storage
import resp.Protocol

public class PropagateRouting(
    private val reader: Reader,
    private val replica: Replica,
    private val repo: Storage,
    private val responder: Responder,
) {
    public fun CoroutineScope.start() {
        launch {
            try {
                while (true) {
                    val command = reader.read(replica.reader)
                    propagateRoutes(command, replica.writer)
                }
            } catch (e: Throwable) {
                println("Connection lost with master $e")
            }
        }
    }

    private suspend fun propagateRoutes(protocol: Protocol, sendChannel: ByteWriteChannel) {
        when (protocol.arguments[0]) {
            "ping" -> {
                commands.Ping().run(protocol)
            }

            "replconf" -> {
                val result = commands.ReplconfAck(replica).run(protocol)
                responder.sendArray(result, sendChannel)
            }

            "set" -> {
                commands.Set(repo, Clock.systemUTC()).run(protocol)
            }

            else -> error("unknown command ${protocol.arguments[0]}")
        }
    }
}
