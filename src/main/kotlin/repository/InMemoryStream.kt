package repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.concurrent.CancellationException

public data class StreamData(
    val streamKey: String,
    val timeStamp: String,
    val data: Map<String, String>,
)

public class InMemoryStream : StreamStorage {
    private val data: MutableMap<String, MutableMap<String, Map<String, String>>> = LinkedHashMap()
    private val arrayFlow: MutableSharedFlow<StreamData> =
        MutableSharedFlow(0)

    public override fun CoroutineScope.set(streamKey: String, timeStamp: String, keyValue: Map<String, String>) {
        val stream = data.getOrPut(streamKey) { LinkedHashMap() }
        stream[timeStamp] = keyValue
        launch {
            arrayFlow.emit(
                StreamData(
                    streamKey,
                    timeStamp,
                    keyValue,
                ),
            )
        }
    }

    public override fun getKey(streamKey: String): String? {
        val stream = data[streamKey]
        return if (stream != null) {
            streamKey
        } else {
            null
        }
    }

    public override fun latestTimeStamp(streamKey: String): String {
        val stream = data[streamKey]
        return stream?.entries?.last()?.key ?: "0-0"
    }

    override fun getByRange(
        streamKey: String,
        minTime: Int,
        maxTime: Int,
        minSequence: Int,
        maxSequence: Int,
    ): MutableMap<String, Map<String, String>> {
        val result = mutableMapOf<String, Map<String, String>>()
        val stream = data[streamKey] ?: return result
        stream.forEach { data ->
            val rawTimeStamp = data.key.splitTimeStamp()
            val timeStamp = rawTimeStamp[0].toInt()
            val sequence = rawTimeStamp[1].toInt()
            if (timeStamp in minTime..maxTime) {
                if (timeStamp in (minTime + 1)..<maxTime) {
                    result[data.key] = data.value
                } else if (sequence in minSequence..maxSequence) {
                    result[data.key] = data.value
                }
            }
        }

        return result
    }

    override fun getByStart(
        streamKey: String,
        minTime: Int,
        minSequence: Int,
    ): MutableMap<String, Map<String, String>> {
        val result = mutableMapOf<String, Map<String, String>>()
        val stream = data[streamKey] ?: return result
        stream.forEach { data ->
            val rawTimeStamp = data.key.splitTimeStamp()
            val timeStamp = rawTimeStamp[0].toInt()
            val sequence = rawTimeStamp[1].toInt()
            if (timeStamp > minTime) {
                result[data.key] = data.value
            } else if (timeStamp == minTime && sequence > minSequence) {
                result[data.key] = data.value
            }
        }

        return result
    }

    override suspend fun blockRead(
        streamKey: String,
        timeLimit: Long,
        minTime: Int,
        minSequence: Int,
        getNewest: Boolean,
    ): MutableMap<String, Map<String, String>> {
        if (getNewest) {
            val existingResult = getByStart(streamKey, minTime, minSequence)
            if (existingResult.isNotEmpty()) {
                return existingResult
            }
        }

        val result: MutableMap<String, Map<String, String>> = mutableMapOf()
        try {
            withTimeout(timeLimit) {
                while (isActive) {
                    arrayFlow.collect { update ->
                        println("got collection $update")
                        if (update.streamKey != streamKey) {
                            return@collect
                        }
                        val rawTimeStamp = update.timeStamp.splitTimeStamp()
                        val timeStamp = rawTimeStamp[0].toInt()
                        val sequence = rawTimeStamp[1].toInt()
                        println("matching timeStamp $timeStamp, minTime $minTime, $sequence, $minSequence")
                        if (timeStamp > minTime) {
                            result[streamKey] = update.data
                            throw CancellationException("Condition met, cancelling collection")
                        } else if (timeStamp == minTime && sequence > minSequence) {
                            result[update.timeStamp] = update.data
                            println("returning $result")
                            throw CancellationException("Condition met, cancelling collection")
                        }
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            println("returning with timeout time was $timeLimit")
            return mutableMapOf()
        } catch (e: CancellationException) {
            return result
        }

        return result
    }
}

public fun String.splitTimeStamp(): List<String> {
    return if (this == "-") {
        listOf("-")
    } else {
        this.split("-")
    }
}
