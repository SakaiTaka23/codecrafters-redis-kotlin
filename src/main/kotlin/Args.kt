private const val DEFAULT_REDIS_PORT = 6379

public data class Args(
    public val port: Int,
    public val isSlave: Boolean,
    public val masterHost: String?,
    public val masterPort: Int?,
    public val dir: String,
    public val dbfilename: String,
    public val replID: String = "8371b4fb1155b71f4a04d3e1bc3e18c4a990aeeb",
    public val replOffset: Int = 0,
)

public fun Array<String>.checkArgs(): Args {
    var port = DEFAULT_REDIS_PORT
    var isSlave = false
    var masterHost: String? = null
    var masterPort: Int? = null
    var dir = ""
    var dbfilename = ""

    for (i in this.indices) {
        if (this[i] == "--port" && i + 1 < this.size) {
            this[i + 1].toIntOrNull()?.let {
                port = it
            }
        }
        if (this[i] == "--replicaof" && i + 2 <= this.size) {
            isSlave = true
            val hostAndPort = this[i + 1].split(" ")
            masterHost = hostAndPort[0]
            hostAndPort[1].toIntOrNull()?.let {
                masterPort = it
            }
        }
        if (this[i] == "--dir" && i + 1 <= this.size) {
            dir = this[i + 1]
        }
        if (this[i] == "--dbfilename" && i + 1 <= this.size) {
            dbfilename = this[i + 1]
        }
    }

    return Args(port, isSlave, masterHost, masterPort, dir, dbfilename)
}
