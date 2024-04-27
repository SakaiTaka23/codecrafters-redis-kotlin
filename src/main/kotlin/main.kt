import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val REDIS_PORT = 6379

public suspend fun main() {
    coroutineScope {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", REDIS_PORT)
        println("Server started at ${serverSocket.localAddress}")

        while (true) {
                val socket = serverSocket.accept()
                println("Accepted $socket")
            launch {
                val receiveChannel = socket.openReadChannel()
                val sendChanel = socket.openWriteChannel(autoFlush = true)
                try {
                    while (true) {
                        val command = receiveChannel.readUTF8Line()
                        routes.Routing().defineRoutes(command, sendChanel)
                    }
                } catch (e: Throwable) {
                    println("Connection lost $e")
                    socket.close()
                }
            }
        }
    }
}
