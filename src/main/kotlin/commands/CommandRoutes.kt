package commands

import resp.Protocol

public interface CommandRoutes {
    public fun run(protocol: Protocol): Protocol
}
