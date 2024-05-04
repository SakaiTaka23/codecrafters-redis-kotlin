package commands

import resp.Protocol

public class Wait : CommandRoutes {
    override fun run(protocol: Protocol): Protocol = Protocol(mutableListOf("0"))
}
