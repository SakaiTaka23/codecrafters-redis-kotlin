package commands

import repository.StreamStorage
import repository.splitTimeStamp
import resp.Entry
import resp.Protocol
import resp.StreamEntry

public class XRead(private val repo: StreamStorage) {
    public suspend fun run(protocol: Protocol): List<StreamEntry> {
        val blockTimeout = protocol.arguments[1].let {
            if (it == "block") {
                val out = protocol.arguments.removeAt(2)
                protocol.arguments.removeAt(1)
                out.toLongOrNull()
            } else {
                null
            }
        }
        val validatedTimeout = blockTimeout.let {
            if (it?.toInt() == 0) {
                Long.MAX_VALUE
            } else {
                it
            }
        }
        val keys = protocol.readKeys()
        val result = mutableListOf<StreamEntry>()

        when (validatedTimeout) {
            null -> {
                keys.forEach {
                    val rawTime = it.value.splitTimeStamp()
                    val streamResult = repo.getByStart(it.key, rawTime[0].toInt(), rawTime[1].toInt()).formatResult()
                    result.add(StreamEntry(it.key, streamResult))
                }
            }

            else -> {
                keys.forEach {
                    val rawTime = it.value.splitTimeStamp()
                    val streamResult =
                        repo.blockRead(it.key, validatedTimeout, rawTime[0].toInt(), rawTime[1].toInt()).formatResult()
                    if (streamResult.isNotEmpty()) {
                        result.add(StreamEntry(it.key, streamResult))
                    }
                }
            }
        }

        return result
    }
}

private fun Protocol.readKeys(): Map<String, String> {
    val result: MutableMap<String, String> = mutableMapOf()
    this.arguments.removeAt(0)
    this.arguments.removeAt(0)

    val keyValues = this.arguments
    val keyCount = keyValues.size / 2

    keyValues.forEachIndexed { index, key ->
        if (index < keyCount) {
            result[key] = keyValues[index + keyCount]
        }
    }

    return result
}

private fun MutableMap<String, Map<String, String>>.formatResult(): List<Entry> {
    val result: MutableList<Entry> = mutableListOf()
    this.forEach { data ->
        val entry = Entry(data.key, data.value.flattenMap())
        result.add(entry)
    }
    return result
}

private fun Map<String, String>.flattenMap(): List<String> = this.flatMap { (key, value) -> listOf(key, value) }
