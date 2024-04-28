package reciever

import io.ktor.utils.io.ByteReadChannel

public class Arguments(private val source: ByteReadChannel) {
    public suspend fun read(argCount: Int): MutableList<String> {
        val argList = mutableListOf<String>()
        val loopCount = argCount - 1

        repeat(loopCount) {
            val argLen = source.readUTF8Line(10)?.removePrefix("$")?.toInt() ?: error("closed")
            val arg = source.readUTF8Line(argLen) ?: error("closed")

            if (arg.length != argLen) {
                error("Invalid argument")
            }

            argList.add(arg)
        }

        return argList
    }
}
