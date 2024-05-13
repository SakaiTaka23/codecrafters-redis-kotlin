
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
import presentor.Responder
import rdbreader.Parser
import reciever.Reader
import replicator.HandShake
import replicator.Propagator
import repository.InMemory
import resp.Decoder
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

    val propagateResultChannel = Channel<Int>(Channel.UNLIMITED)
    val propagateChannel = Channel<Protocol>(Channel.UNLIMITED)

    val server = Server(
        inputConfig.port,
        inputConfig.isSlave,
        inputConfig.replID,
        inputConfig.replOffset,
        inputConfig.dir,
        inputConfig.dbfilename,
        Protocol(mutableListOf()),
        propagateResultChannel,
        mutableListOf(),
    )

    if (inputConfig.isSlave) {
        HandShake(inputConfig.port, Responder, Reader(Decoder)).run(inputConfig.masterHost, inputConfig.masterPort)
    }
    val propagator = Propagator(server, Responder, propagateChannel)
    val reader = Reader(Decoder)
    val readPropagateJob = Job()

    coroutineScope {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", inputConfig.port)
        println("Server started at ${serverSocket.localAddress}")
        if (inputConfig.isSlave) {
            println("Server is in slave mode connecting to ${inputConfig.masterHost}:${inputConfig.masterPort}")
            launch {
                with(Routing(
                    propagateChannel,
                    propagator,
                    reader,
                    storage,
                    Responder,
                    server,
                    serverSocket
                )) {
                    CoroutineScope(readPropagateJob).readPropagate(Replica.getInstance())
                }
            }
        } else {
            launch { propagator.run() }
        }
        launch {
            Routing(
                propagateChannel,
                propagator,
                reader,
                storage,
                Responder,
                server,
                serverSocket
            ).start()
        }
    }
}
