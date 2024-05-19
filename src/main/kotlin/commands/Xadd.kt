package commands

import repository.StreamStorage
import resp.Protocol

private const val COUNT_TO_KEY_VALUE = 3

public class Xadd(private val repo: StreamStorage) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol {
        val streamKey = protocol.arguments[1]
        val rawTimeStamp = protocol.arguments[2]
        val timeStamp = rawTimeStamp.splitTimeStamp()
        val keyValue = mutableMapOf<String, String>()
        val arguments = protocol.arguments.drop(COUNT_TO_KEY_VALUE)

        val milliSeconds = timeStamp[0].toIntOrNull() ?: return Protocol(mutableListOf())
        val sequenceNumber = timeStamp[1].let {
            if (it == "*") {
                Int.MAX_VALUE
            } else {
                it.toIntOrNull() ?: return Protocol(mutableListOf())
            }
        }
        val latestTimeStamp = repo.latestTimeStamp(streamKey).splitTimeStamp()

        if (!validateMinimum(milliSeconds, sequenceNumber)) {
            return Protocol(
                mutableListOf("ERR The ID specified in XADD must be greater than 0-0"),
            )
        }
        if (!validateTimestamp(milliSeconds, sequenceNumber, latestTimeStamp)) {
            return Protocol(
                mutableListOf("ERR The ID specified in XADD is equal or smaller than the target stream top item"),
            )
        }

        val setTimeStamp = generateSequence(milliSeconds, sequenceNumber, latestTimeStamp, rawTimeStamp)

        for (i in arguments.indices step 2) {
            if (i + 1 < arguments.size) {
                keyValue[arguments[i]] = arguments[i + 1]
            }
        }
        repo.set(streamKey, setTimeStamp, keyValue.toMap())

        return Protocol(mutableListOf(setTimeStamp))
    }

    private fun generateSequence(
        milliSeconds: Int,
        sequenceNumber: Int,
        latestTimeStamp: List<String>,
        rawTimeStamp: String,
    ): String {
        if (sequenceNumber == Int.MAX_VALUE) {
            return when (milliSeconds) {
                latestTimeStamp[0].toInt() -> "$milliSeconds-${latestTimeStamp[1].toInt() + 1}"
                0 -> "0-1"
                else -> "$milliSeconds-0"
            }
        }

        return rawTimeStamp
    }

    private fun validateMinimum(milliSeconds: Int, sequenceNumber: Int): Boolean =
        !(milliSeconds == 0 && sequenceNumber == 0)

    private fun validateTimestamp(
        milliSeconds: Int,
        sequenceNumber: Int,
        latestTimeStamp: List<String>,
    ): Boolean = if (latestTimeStamp[0].toInt() > milliSeconds) {
        false
    } else if (latestTimeStamp[0].toInt() == milliSeconds) {
        latestTimeStamp[1].toInt() < sequenceNumber
    } else {
        true
    }
}

private fun String.splitTimeStamp(): List<String> = this.split("-")
