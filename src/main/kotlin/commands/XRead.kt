package commands

import repository.StreamStorage
import repository.splitTimeStamp
import resp.Entry
import resp.Protocol
import resp.StreamEntry

public class XRead(private val repo: StreamStorage) {
    public fun run(protocol: Protocol): List<StreamEntry> {
        val streamKey = protocol.arguments[2]
        val startTimestamp = protocol.arguments[3].splitTimeStamp()
        val minTime = startTimestamp[0].toInt()
        val minSequence = startTimestamp[1].toInt()
        val result = mutableListOf<StreamEntry>()

        val streamResult = repo.getByStart(streamKey, minTime, minSequence).formatResult()
        result.add(StreamEntry(streamKey, streamResult))

        return result
    }
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
