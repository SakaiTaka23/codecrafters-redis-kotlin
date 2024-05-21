package commands

import repository.StreamStorage
import repository.splitTimeStamp
import resp.Entry
import resp.Protocol
import resp.StreamEntry

public class XRead(private val repo: StreamStorage) {
    public fun run(protocol: Protocol): List<StreamEntry> {
        val keys = protocol.readKeys()
        val result = mutableListOf<StreamEntry>()

        keys.forEach {
            val rawTime = it.value.splitTimeStamp()
            val streamResult = repo.getByStart(it.key, rawTime[0].toInt(), rawTime[1].toInt()).formatResult()
            result.add(StreamEntry(it.key, streamResult))
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
            result.put(key, keyValues[index + keyCount])
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
