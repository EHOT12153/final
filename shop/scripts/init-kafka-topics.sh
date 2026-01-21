#!/usr/bin/env bash
set -e
BROKER=${1:-kafka:9092}
for t in order.created order.paid order.failed inventory.reserved inventory.released payment.requested payment.confirmed; do
  kafka-topics.sh --bootstrap-server "$BROKER" --create --if-not-exists --topic "$t" --replication-factor 1 --partitions 3 || true
done
