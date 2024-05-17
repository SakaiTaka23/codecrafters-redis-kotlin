package commands

import repository.Storage
import repository.StreamStorage
import resp.Protocol

public class Type(private val repo: Storage, private val streamRepo: StreamStorage) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol {
        val value = repo.get(protocol.arguments[1])
        val svalue = streamRepo.getKey(protocol.arguments[1])

        val resultType = when {
            value != null -> "string"
            svalue != null -> "stream"
            else -> "none"
        }

        return Protocol(mutableListOf(resultType))
    }
}
