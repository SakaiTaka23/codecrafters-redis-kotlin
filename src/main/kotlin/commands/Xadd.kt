package commands

import repository.StreamStorage
import resp.Protocol

private const val COUNT_TO_KEY_VALUE = 3

public class Xadd(private val repo: StreamStorage) : CommandRoutes {
    override fun run(protocol: Protocol): Protocol {
        val streamKey = protocol.arguments[1]
        val timeStamp = protocol.arguments[2]
        val keyValue = mutableMapOf<String, String>()
        val arguments = protocol.arguments.drop(COUNT_TO_KEY_VALUE)

        if (!validateMinimum(timeStamp)) {
            return Protocol(
                mutableListOf("ERR The ID specified in XADD must be greater than 0-0"),
            )
        }
        if (!validateTimestamp(streamKey, timeStamp)) {
            return Protocol(
                mutableListOf("ERR The ID specified in XADD is equal or smaller than the target stream top item"),
            )
        }

        for (i in arguments.indices step 2) {
            if (i + 1 < arguments.size) {
                keyValue[arguments[i]] = arguments[i + 1]
            }
        }
        repo.set(streamKey, timeStamp, keyValue.toMap())

        return Protocol(mutableListOf(timeStamp))
    }

    private fun validateMinimum(rawTimeString: String): Boolean {
        val input = rawTimeString.splitTimeStamp()
        val milliSeconds = input[0].toIntOrNull() ?: return false
        val sequenceNumber = input[1].toIntOrNull() ?: return false
        return !(milliSeconds == 0 && sequenceNumber == 0)
    }

    private fun validateTimestamp(streamKey: String, rawTimeString: String): Boolean {
        val latest = repo.latestTimeStamp(streamKey).splitTimeStamp()
        val input = rawTimeString.splitTimeStamp()
        val milliSeconds = input[0].toIntOrNull() ?: return false
        val sequenceNumber = input[1].toIntOrNull() ?: return false

        return if (latest[0].toInt() > milliSeconds) {
            false
        } else if (latest[0].toInt() == milliSeconds) {
            latest[1].toInt() < sequenceNumber
        } else {
            true
        }
    }
}

private fun String.splitTimeStamp(): List<String> = this.split("-")
