#!/usr/bin/env bash
# usage: ./wait-for.sh host:port timeout_seconds
set -e
H=${1:-localhost:8080}; T=${2:-60}
echo "Waiting for $H up to $T s..."
for i in $(seq 1 $T); do
  nc -z $(echo $H|cut -d: -f1) $(echo $H|cut -d: -f2) && exit 0
  sleep 1
done
echo "Timeout"; exit 1
