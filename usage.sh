#!/bin/zsh
set -e

while true
do
  curl -s -H"Accept: application/json" localhost:8080/q/metrics/base | jq '."memory.usedHeap"' | awk '{$1/=1024;printf "%.2fKB\n",$1}' | awk '{$1/=1024;printf "%.2fMB\n",$1}'
  sleep 0.5
done

