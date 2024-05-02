package commands

import resp.Protocol

public class Replconf : CommandRoutes {
    override fun run(protocol: Protocol): Protocol = Protocol(mutableListOf("OK"))
}
