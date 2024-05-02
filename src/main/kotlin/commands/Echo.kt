package commands

import resp.Protocol

public class Echo : CommandRoutes {
    override fun run(protocol: Protocol): Protocol = Protocol(mutableListOf(protocol.arguments[1]))
}
