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
import rdbreader.parseRDB
import reciever.Reader
import replicator.HandShake
import replicator.Propagator
import repository.InMemory
import repository.InMemoryStream
import resp.Decoder
import resp.Protocol
import routes.PropagateRouting
import routes.Routing

public suspend fun main(args: Array<String>) {
    val storage = InMemory()
    val inputConfig = args.checkArgs()
    if (inputConfig.dir.isNotBlank() && inputConfig.dbfilename.isNotBlank()) {
        val datas = parseRDB(inputConfig.dir, inputConfig.dbfilename)
        datas.forEach { data ->
            val expire = data.value.second
            if (expire != null) {
                println("setting data with expiration: key ${data.key}, value: ${data.value.first}, expire: $expire")
                storage.set(data.key, data.value.first, expire)
            } else {
                println("setting data without expiration: key ${data.key}, value: ${data.value.first}")
                storage.set(data.key, data.value.first)
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
    val propagator = Propagator(server, Responder, propagateChannel)
    val streamRepo = InMemoryStream()
    val reader = Reader(Decoder)
    val readPropagateJob = Job()

    coroutineScope {
        val selectorManager = SelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", inputConfig.port)
        println("Server started at ${serverSocket.localAddress}")
        if (inputConfig.isSlave) {
            val masterConnection = HandShake(inputConfig.port, Responder, Reader(Decoder)).run(
                inputConfig.masterHost,
                inputConfig.masterPort,
            )
            println("Server is in slave mode connecting to ${inputConfig.masterHost}:${inputConfig.masterPort}")
            launch {
                with(
                    PropagateRouting(
                        reader,
                        masterConnection,
                        storage,
                        Responder,
                    ),
                ) {
                    CoroutineScope(readPropagateJob).start()
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
                serverSocket,
                streamRepo,
            ).start()
        }
    }
}
