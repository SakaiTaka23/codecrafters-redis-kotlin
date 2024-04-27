package parser

import global.ParsedCommand

public class Command(private val command: String?) {
    public fun parse(): ParsedCommand? {
        if (command == null) {
            return null
        }

        val commandListTmp: String
        if (command.startsWith("redis-cli")) {
            commandListTmp = command.split("redis-cli")[1]
        } else {
            commandListTmp = command
        }

        val commandList = commandListTmp
                .split("""\r\n""", " ")
                .filter { it != "" }
        println("commandList: $commandList, commandList.size: ${commandList.size}")

        if (commandList.size < 3 || !commandList[0].contains("*")) {
            return ParsedCommand(
                commandCount = commandList.size,
                mainCommand = commandList.elementAtOrNull(0)?.lowercase(),
                mainCommandLen = commandList.elementAtOrNull(0)?.length?: 0,
                subArg = commandList.elementAtOrNull(1),
                subArgLen = commandList.elementAtOrNull(1)?.length?: 0
            )
        }

        return ParsedCommand(
            commandCount = 2,
            mainCommand = commandList[2].lowercase(),
            mainCommandLen = commandList[1].substring(1).toInt(),
            subArg = commandList.elementAtOrNull(4),
            subArgLen = commandList.elementAtOrNull(3)?.substring(1)?.toInt()?: 0
        )
    }
}
