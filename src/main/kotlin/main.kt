import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import routes.Routing

private const val REDIS_PORT = 6379

public suspend fun main() {
    coroutineScope {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", REDIS_PORT)
        println("Server started at ${serverSocket.localAddress}")
        Routing(serverSocket).start()
    }
}
