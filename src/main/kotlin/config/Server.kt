package config

import org.koin.core.component.KoinComponent

private const val DEFAULT_REDIS_PORT = 6379

public class Server(args: Array<String>) : KoinComponent {
    public var port: Int = DEFAULT_REDIS_PORT
    public var isSlave: Boolean = false
    public var masterHost: String? = null
    public var masterPort: Int? = null

    init {
        checkOptions(args)
    }

    private fun checkOptions(args: Array<String>) {
        for (i in args.indices) {
            if (args[i] == "--port" && i + 1 < args.size) {
                args[i + 1].toIntOrNull()?.let {
                    port = it
                }
            }
            if (args[i] == "--replicaof" && i + 2 <= args.size) {
                isSlave = true
                masterHost = args[i + 1]
                args[i + 2].toIntOrNull()?.let {
                    masterPort = it
                }
            }
        }
    }
}
