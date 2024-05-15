#!/bin/sh
set -e
exec java -jar ../codecrafters-redis-kotlin/target/java_redis.jar "$@"
