package commands

import resp.Protocol

public class Ping : CommandRoutes {
    override fun run(protocol: Protocol): Protocol = Protocol(mutableListOf("PONG"))
}
