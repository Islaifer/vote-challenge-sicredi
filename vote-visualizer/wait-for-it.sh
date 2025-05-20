#!/usr/bin/env bash

host="$1"
port="$2"
shift 2
cmd="$@"

until nc -z "$host" "$port"; do
  echo "Waiting $host:$port..."
  sleep 2
done

>&2 echo "$host:$port is up, starting application."
exec $cmd