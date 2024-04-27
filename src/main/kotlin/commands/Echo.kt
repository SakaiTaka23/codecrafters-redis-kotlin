package commands

public class Echo {
    public fun run(command: String): String {
        return "$command\r\n"
    }
}
