import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import org.koin.core.context.startKoin
import routes.Routing

public suspend fun main(args: Array<String>) {
    var redisPort = 6379
    for (i in args.indices) {
        if (args[i] == "--port" && i + 1 < args.size) {
            redisPort = args[i + 1].toIntOrNull() ?: redisPort
        }
    }

    startKoin {
        modules(appModule)
    }

    coroutineScope {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", redisPort)
        println("Server started at ${serverSocket.localAddress}")
        Routing(receiveModule, respondModule, serverSocket).start()
    }
}
