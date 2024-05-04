package commands

import resp.Protocol

public class Replconf : CommandRoutes {
    override fun run(protocol: Protocol): Protocol = Protocol(mutableListOf("OK"))
}

public class ReplconfAck : CommandRoutes {
    override fun run(protocol: Protocol): Protocol {
        if (protocol.arguments[1] == "GETACK") {
            return Protocol(mutableListOf("REPLCONF", "ACK", "0"))
        } else {
            error("invalid command")
        }
    }
}
