package commands

import repository.StreamStorage
import repository.splitTimeStamp
import resp.Entry
import resp.Protocol

public class XRange(private val repo: StreamStorage) {
    public fun run(protocol: Protocol): List<Entry> {
        val streamKey = protocol.arguments[1]
        val startTimestamp = protocol.arguments[2].splitTimeStamp()
        val endTimestamp = protocol.arguments[3].splitTimeStamp()

        val minTime = startTimestamp[0].let {
            if (it == "-") {
                0
            } else {
                it.toInt()
            }
        }
        val maxTime = endTimestamp[0].let {
            if (it == "+") {
                Int.MAX_VALUE
            } else {
                it.toInt()
            }
        }
        val minSequence = startTimestamp.getOrNull(1)?.toIntOrNull() ?: 0
        val maxSequence = endTimestamp.getOrNull(1)?.toIntOrNull() ?: Int.MAX_VALUE

        println("called with getByRange $streamKey, $minTime, $maxTime, $minSequence, $maxSequence")
        val result = repo.getByRange(streamKey, minTime, maxTime, minSequence, maxSequence)
        return result.formatResult()
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
