import config.Replica
import config.Server
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentor.Responder
import rdbreader.Parser
import reciever.Reader
import replicator.HandShake
import replicator.Propagator
import repository.InMemory
import resp.Protocol
import routes.Routing

public suspend fun main(args: Array<String>) {
    val storage = InMemory()
    val inputConfig = args.checkArgs()
    if (inputConfig.dir.isNotBlank() && inputConfig.dbfilename.isNotBlank()) {
        val datas = Parser.read(inputConfig.dir, inputConfig.dbfilename)

        datas.forEach { data ->
            val expire = data.value.getExpiredDate()
            if (expire != null) {
                storage.set(data.key, data.value.getValue(), data.value.getExpiredDate())
            } else {
                storage.set(data.key, data.value.getValue())
            }
        }
    }

    val server = Server.getInstance(
        inputConfig.port,
        inputConfig.isSlave,
        inputConfig.replID,
        inputConfig.replOffset,
        inputConfig.dir,
        inputConfig.dbfilename,
        Protocol(mutableListOf()),
        Channel(Channel.UNLIMITED),
        mutableListOf(),
    )

    if (inputConfig.isSlave) {
        HandShake(inputConfig.port, Responder, Reader()).run(inputConfig.masterHost, inputConfig.masterPort)
    }
    val propagator = Propagator()
    val readPropagateJob = Job()

    startKoin {
        modules(
            appModule,
            propagateModule,
            readerModule,
            responderModule,
            module {
                single { server }
            }
        )
    }

    coroutineScope {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", inputConfig.port)
        println("Server started at ${serverSocket.localAddress}")
        if (inputConfig.isSlave) {
            println("Server is in slave mode connecting to ${inputConfig.masterHost}:${inputConfig.masterPort}")
            launch {
                with(Routing(serverSocket)) {
                    CoroutineScope(readPropagateJob).readPropagate(Replica.getInstance())
                }
            }
        } else {
            launch { propagator.run() }
        }
        launch {
            Routing(serverSocket).start()
        }
    }
}
