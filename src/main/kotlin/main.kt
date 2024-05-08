import config.Server
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import replicator.HandShake
import replicator.Propagator
import routes.Routing

public suspend fun main(args: Array<String>) {
    startKoin {
        modules(
            appModule,
            propagateModule,
            readerModule,
            responderModule,
            module {
                single { Server(args) }
            }
        )
    }

    val server = GlobalContext.get().get<Server>()
    val propagator = GlobalContext.get().get<Propagator>()

    coroutineScope {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", server.port)
        println("Server started at ${serverSocket.localAddress}")
        if (server.isSlave) {
            println("Server is in slave mode connecting to ${server.masterHost}:${server.masterPort}")
            launch {
                HandShake().run()
                Routing(serverSocket).readPropagate()
            }
        } else {
            launch { propagator.run() }
        }
        launch {
            Routing(serverSocket).start()
        }
    }
}
