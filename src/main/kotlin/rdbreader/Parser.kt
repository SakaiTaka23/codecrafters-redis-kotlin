package rdbreader

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.time.Instant

public class EndOfRdbFileException : java.lang.RuntimeException()

public class CacheValue(private var value: String) {
    private var expiryDate: Instant? = null

    public fun setExpiredDate(expiryDate: Instant) {
        this.expiryDate = expiryDate
    }

    public fun getExpiredDate(): Instant? = this.expiryDate

    public fun getValue(): String = this.value
}

@Suppress("MagicNumber")
public object Parser {
    public fun read(dir: String, dbfilename: String): Map<String, CacheValue> {
        val inputStream = loadFile(dir, dbfilename) ?: return mapOf()

        inputStream.use { stream ->
            // Skip markers and move to key-value data
            skipUntilKeyValue(stream)
            // Skip hash table sizes
            skipHashTableSizes(stream)
            // Read all key-value pairs from the stream
            return readAllPairsFromStream(stream)
        }
    }

    private fun loadFile(dir: String, dbfilename: String): FileInputStream? = try {
        File("$dir/$dbfilename").inputStream()
    } catch (e: FileNotFoundException) {
        println("File was not found")
        null
    }
}

private fun skipUntilKeyValue(inputStream: FileInputStream) {
    var marker: Byte = 0
    while ((marker.toInt() and 0xFB) != 0xFB) {
        marker = inputStream.readByte()
    }
}

private fun skipHashTableSizes(inputStream: FileInputStream) {
    skipLengthEncodedInt(inputStream)
    skipLengthEncodedInt(inputStream)
}

private fun skipLengthEncodedInt(inputStream: FileInputStream) {
    val firstByte = inputStream.readByte()
    if ((firstByte.toInt() and 0x80) == 0) {
        println("First bit for skip is 0. Length is one byte")
    } else if ((firstByte.toInt() and 0x40) == 0) {
        inputStream.readByte()
    } else if (firstByte.toInt() == 0xFF) {
        throw RuntimeException("Unexpected end of RDB file")
    } else {
        val bytesToSkip = when (firstByte.toInt() and 0x3F) {
            0 -> 1
            1 -> 2
            2 -> 4
            else -> throw RuntimeException("Invalid encoding in RDB file")
        }
        inputStream.readNBytes(bytesToSkip)
    }
}

@Throws(IOException::class)
private fun readAllPairsFromStream(inputStream: FileInputStream): Map<String, CacheValue> {
    val pairs: MutableMap<String, CacheValue> = HashMap()
    try {
        while (true) {
            val keyValuePair: Pair<String, CacheValue> = readKeyValuePair(inputStream) // Read key-value pair
            pairs[keyValuePair.first] = keyValuePair.second // Add pair to the map
        }
    } catch (e: EndOfRdbFileException) {
        println("End of RDB file reached") // Output message if end of file is reached
    }
    return pairs
}

@Throws(IOException::class)
private fun readKeyValuePair(inputStream: FileInputStream): Pair<String, CacheValue> {
    val typeByte = inputStream.readByte()
    if (typeByte == 0xFF.toByte()) {
        throw EndOfRdbFileException() // Throw exception if end of file is reached
    }
    val expiry: Instant = readExpiry(inputStream, typeByte) // Read expiry of the record
    val key: ByteArray = readEncodedString(inputStream) // Read the key
    val value: CacheValue = readValue(inputStream, typeByte) // Read the value
    value.setExpiredDate(expiry)
    return Pair(String(key), value) // Return the key-value pair
}

// Method to read encoded string
@Throws(IOException::class)
private fun readEncodedString(inputStream: FileInputStream): ByteArray {
    val length: Int = readLengthEncodedInt(inputStream) // Read length of the string
    val encodedString = ByteArray(length)
    inputStream.read(encodedString)
    return encodedString
}

// Method to read length of encoded integer
@Throws(IOException::class)
private fun readLengthEncodedInt(inputStream: FileInputStream): Int {
    val firstByte = inputStream.readByte()
    var value = 0
    if ((firstByte.toInt() and 0x80) == 0) {
        value = firstByte.toInt() // If first bit is 0, length is one byte
    } else if ((firstByte.toInt() and 0x40) == 0) {
        value =
            ((firstByte.toInt() and 0x3F) shl 8) + (inputStream.readByte().toInt() and 0xFF) // Length is two bytes
    } else if (firstByte.toInt() == 0xFF) {
        throw java.lang.RuntimeException("Unexpected end of RDB file")
    } else {
        // If first two bits are 10, length is determined by additional bytes
        val bytesToRead = when (firstByte.toInt() and 0x3F) {
            0 -> 1
            1 -> 2
            2 -> 4
            else -> throw java.lang.RuntimeException("Invalid encoding in RDB file")
        }
        for (i in 0 until bytesToRead) {
            value = (value shl 8) + (inputStream.readByte().toInt() and 0xFF) // Read remaining bytes of the integer
        }
    }
    return value
}

// Method to read expiry of the record
@Throws(IOException::class)
private fun readExpiry(inputStream: FileInputStream, typeByte: Byte): Instant =
    if ((typeByte.toInt() and 0xFD) == 0xFD || (typeByte.toInt() and 0xFC) == 0xFC) {
        Instant.ofEpochSecond(inputStream.read().toLong()) // If expiry is in seconds, read it
    } else {
        Instant.MAX // Otherwise, return maximum possible expiry
    }

// Method to read value of the record
@Throws(IOException::class)
private fun readValue(inputStream: FileInputStream, typeByte: Byte): CacheValue {
    if (typeByte.toInt() == 0) {
        return CacheValue(String(readEncodedString(inputStream))) // Read string value
    } else {
        // Throw exception for unsupported value types
        throw java.lang.RuntimeException("Unsupported value type: $typeByte")
    }
}

private fun FileInputStream.readByte() = readNBytes(1)[0]
