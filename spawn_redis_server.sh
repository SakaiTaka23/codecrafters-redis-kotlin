#!/bin/sh
set -e
pwd
exec java -jar ../codecrafters-redis-kotlin/target/java_redis.jar "$@"
