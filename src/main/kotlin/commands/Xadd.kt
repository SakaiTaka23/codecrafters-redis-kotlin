package commands

import repository.StreamStorage
import resp.Protocol

private const val COUNT_TO_KEY_VALUE = 3

public class Xadd(private val repo: StreamStorage) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol {
        val streamKey = protocol.arguments[1]
        val id = protocol.arguments[2]
        val keyValue = mutableMapOf<String, String>()
        val arguments = protocol.arguments.drop(COUNT_TO_KEY_VALUE)

        for (i in arguments.indices step 2) {
            if (i + 1 < arguments.size) {
                keyValue[arguments[i]] = arguments[i + 1]
            }
        }
        repo.set(streamKey, id, keyValue.toMap())

        return Protocol(mutableListOf(id))
    }
}
