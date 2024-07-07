#!/bin/bash

# Define the path to the log file in the container
LOG_FILE="/var/log/spring-boot/spring-boot.log"

# Define the destination (host and port) where the log should be forwarded
DESTINATION="localhost:5044"

# Check if the log file exists
if [ -f "$LOG_FILE" ]; then
  # Use tail to continuously forward the logs to the destination
  tail -F "$LOG_FILE" | nc -q0 $(echo $DESTINATION | tr ':' ' ')
else
  echo "Log file not found: $LOG_FILE"
  exit 1
fi
