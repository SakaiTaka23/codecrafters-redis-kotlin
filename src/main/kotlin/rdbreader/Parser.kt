package rdbreader

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.Instant

private const val MAGIC_NUMBER_BYTES = 5 // 52 45 44 49 53  # "REDIS"
private const val RDB_VERSION_NUMBER_BYTES = 4 // 30 30 30 33 # "0003" => Version 3

private const val AUXILIARY_FIELD_FLAG = 0xFA
private const val DATABASE_SELECTOR_FLAG = 0xFE
private const val DATABASE_RESIZE_FLAG = 0xFB

private const val EXPIRY_TIME_SECONDS_FLAG = 0xFD
private const val EXPIRY_TIME_SECONDS_BYTES = 4
private const val EXPIRY_TIME_MS_FLAG = 0xFC
private const val EXPIRY_TIME_MS_BYTES = 8

private const val STRING_ENCODING_FLAG = 0 // Key value pair -> value type string

private const val END_OF_RDB_FLAG = 0xFF

public fun parseRDB(dir: String, dbfilename: String): Map<String, Pair<String, Instant?>> {
    val input = loadFile(dir, dbfilename) ?: return emptyMap()
    val inputStream = input.inputStream()
    val magic = inputStream.readNBytes(MAGIC_NUMBER_BYTES) // 52 45 44 49 53  # "REDIS"
    if (!magic.contentEquals("REDIS".toByteArray())) {
        throw Exception("Invalid RDB file")
    }
    val version = inputStream.readNBytes(RDB_VERSION_NUMBER_BYTES)
    println("Version: ${version.decodeToString()}")
    var type = inputStream.read()
    while (true) {
        when (type) {
            AUXILIARY_FIELD_FLAG -> {
                val key = readStringEncoded(inputStream)
                val value = readStringEncoded(inputStream)
                println("Auxiliary Key: $key, Value: $value")
            }

            DATABASE_SELECTOR_FLAG -> {
                println("Database selector: ${inputStream.read()}")
            }

            DATABASE_RESIZE_FLAG -> {
                val hashTableSize = readLengthEncoded(inputStream)
                val expireHashTableSize = readLengthEncoded(inputStream)
                println("Resize DB to $hashTableSize, $expireHashTableSize")
            }

            else -> break
        }
        type = inputStream.read()
    }
    var expiry: Instant? = null
    // Got to keys, type contains the key type or expiry type
    val keys = mutableMapOf<String, Pair<String, Instant?>>()
    while (type != END_OF_RDB_FLAG) {
        if (type == EXPIRY_TIME_SECONDS_FLAG) {
            val seconds = Integer.toUnsignedLong(inputStream.readNBytes(EXPIRY_TIME_SECONDS_BYTES).toInt())
            println("Seconds: $seconds")
            expiry = Instant.ofEpochSecond(seconds)
            type = inputStream.read()
        } else if (type == EXPIRY_TIME_MS_FLAG) {
            val milliseconds = inputStream.readNBytes(EXPIRY_TIME_MS_BYTES).toLong()
            println("Milliseconds: $milliseconds")
            expiry = Instant.ofEpochMilli(milliseconds)
            type = inputStream.read()
        }
        val key = readStringEncoded(inputStream)
        val value = when (type) {
            STRING_ENCODING_FLAG -> readStringEncoded(inputStream)
            else -> throw Exception("Invalid encoding type: $type")
        }
        println("Read key: $key, value: $value, expiry: $expiry")
        keys[key] = value to expiry
        type = inputStream.read()
    }
    return keys
}

private fun loadFile(dir: String, dbfilename: String): ByteArray? = try {
    File("$dir/$dbfilename").readBytes()
} catch (e: FileNotFoundException) {
    println("File was not found reading file: $dir/$dbfilename")
    null
}

private fun readStringEncoded(inputStream: InputStream): String {
    val length = readLengthEncoded(inputStream)
    if (length == 0) {
        return ""
    }
    val chars = inputStream.readNBytes(length)
    return chars.decodeToString()
}

@Suppress("MagicNumber")
private fun readLengthEncoded(inputStream: InputStream): Int {
    val value = inputStream.readNBytes(1)[0]
    val masked = readLineEncodedFlag(value)
    return when (masked) {
        0u -> value.toInt()
        1u -> {
            val firstValue = (value.toInt() and 63.toByte().toInt()).toByte()
            val secondValue = inputStream.readNBytes(1)[0]
            return ((firstValue.toInt() shl 8) or (secondValue.toInt() and 0xFF)).toShort().toInt()
        }

        2u -> inputStream.readNBytes(4).toInt()
        3u -> {
            when (val type = value.toUInt() and 0x3fu) {
                // Signed integer, 8 bits
                0u -> {
                    inputStream.readNBytes(1)
                    0
                }

                1u -> { // Signed integer, 16 bits
                    inputStream.readNBytes(2)
                    0
                }

                2u -> { // Signed integer, 32 bits
                    inputStream.readNBytes(4)
                    0
                }

                3u -> throw UnsupportedOperationException("Cant read special LZF string$type")
                else -> throw UnsupportedOperationException("Unknown special encoding$type")
            }
        }

        else -> throw UnsupportedOperationException("Dont know how to read special length encodings")
    }
}

@Suppress("MagicNumber")
private fun readLineEncodedFlag(value: Byte): UInt = (value.toUInt() and 0xc0u) shr 6

private fun ByteArray.toInt(): Int {
    return ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).getInt()
}

private fun ByteArray.toLong(): Long {
    return ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).getLong()
}
