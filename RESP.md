# RESP (Redis Protocol) Specification

## Example
```shell
*2\r\n$4\r\necho\r\n$3\r\nhey\r\n
```
- *2 : Number of arguments
- $4 : Length of the command of argument
- echo : Command of argument
'\r\n' : Carriage return and line feed

## Types
RESP bulk string
```shell
$<length>\r\n<content>\r\n"
```
RESP simple string
```shell
+<content>\r\n
```
