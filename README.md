[![progress-banner](https://backend.codecrafters.io/progress/redis/5493f2f0-b7b4-459a-9f60-e9c58f61b731)](https://app.codecrafters.io/users/SakaiTaka23?r=2qF)

This is a starting point for Java solutions to the
["Build Your Own Redis" Challenge](https://codecrafters.io/challenges/redis).

In this challenge, you'll build a toy Redis clone that's capable of handling
basic commands like `PING`, `SET` and `GET`. Along the way we'll learn about
event loops, the Redis protocol and more.

**Note**: If you're viewing this repo on GitHub, head over to
[codecrafters.io](https://codecrafters.io) to try the challenge.

# Usage
```shell
telnet 127.0.0.1 6379
```
```shell
redis-cli *1\r\n$4\r\nping\r\n

redis-cli *2\r\n$4\r\necho\r\n$3\r\nhey\r\n
```
