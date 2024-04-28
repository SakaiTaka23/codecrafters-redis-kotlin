import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
                        val buffer = ByteBuffer.allocate(1024)
                        val bytesRead = receiveChannel.readAvailable(buffer)
                        if (bytesRead == -1) {
                            println("Connection lost")
                            break
                        }
                        buffer.flip()
                        val command = StandardCharsets.UTF_8.decode(buffer).toString().trimEnd('\r', '\n')
                        println("command: $command")
                        val parsedCommand = parser.Command(command).parse() ?: continue
                        routes.Routing().defineRoutes(parsedCommand, sendChanel)

//                        val builder = StringBuilder()
//                        while (true) {
//                            val line = receiveChannel.readUTF8Line(50)
//                            println("got line $line")
//                            if (line == null) {
//                                println("Connection lost")
//                                break
//                            }
//                            builder.append(line)
//                            if (!line.endsWith("\\\r\\\n")){
//                                break
//                            }
//                        }
//                        val command = builder.toString()
//                        println("command: $command")
//                        val parsedCommand = parser.Command(command).parse() ?: continue
//                        routes.Routing().defineRoutes(parsedCommand, sendChanel)

//                        val command = parser.Reader(receiveChannel).read()
//                        val parsedCommand = parser.Command(command).parse() ?: continue
//                        routes.Routing().defineRoutes(parsedCommand, sendChanel)
                    }
                } catch (e: Throwable) {
                    println("Connection lost $e")
                    socket.close()
                }
            }
        }
    }
}
