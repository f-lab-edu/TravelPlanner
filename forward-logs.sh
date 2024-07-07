#!/bin/bash

# Define the path to the log file in the mounted volume
LOG_FILE="/var/lib/docker/volumes/travelplanner_spring-boot-logs/_data/roamgram.log"

# Define the destination (host and port) where the log should be forwarded
DESTINATION="localhost:5044"

# Define a temporary file to store logs before sending
TEMP_FILE="/tmp/temp_roamgram.log"

# Check if the log file exists
if [ -f "$LOG_FILE" ]; then
  # Move the log content to a temporary file and clear the original log file
  mv "$LOG_FILE" "$TEMP_FILE"
  : > "$LOG_FILE"

  # Check if the temporary file is not empty
  if [ -s "$TEMP_FILE" ]; then
    # Send the logs to the destination
    cat "$TEMP_FILE" | nc -q0 $(echo $DESTINATION | tr ':' ' ')

    # Remove the temporary file after sending
    rm "$TEMP_FILE"
  fi
else
  echo "Log file not found: $LOG_FILE"
  exit 1
fi
