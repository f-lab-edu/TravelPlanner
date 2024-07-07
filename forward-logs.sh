#!/bin/bash

LOG_FILE="/var/lib/docker/volumes/travelplanner_spring-boot-logs/_data/roamgram.log"
DESTINATION="localhost:5044"
TIMESTAMP_FILE="/tmp/roamgram_last_timestamp"
TEMP_FILE=$(mktemp)

# Copy the log file to a temporary location
cp "$LOG_FILE" "$TEMP_FILE"

# Get the last timestamp
if [ -f "$TIMESTAMP_FILE" ]; then
    LAST_TIMESTAMP=$(cat "$TIMESTAMP_FILE")
else
    LAST_TIMESTAMP=""
fi

# Extract new log entries
if [ -n "$LAST_TIMESTAMP" ]; then
    NEW_LOGS=$(awk -v last_timestamp="$LAST_TIMESTAMP" '$0 > last_timestamp {print $0}' "$TEMP_FILE")
else
    NEW_LOGS=$(cat "$TEMP_FILE")
fi

# Send new log entries
if [ -n "$NEW_LOGS" ]; then
    echo "$NEW_LOGS" | nc -q0 $(echo $DESTINATION | tr ':' ' ')
fi

# Update the last timestamp
LAST_TIMESTAMP=$(tail -n 1 "$TEMP_FILE" | awk '{print $1, $2}')
echo "$LAST_TIMESTAMP" > "$TIMESTAMP_FILE"

# Clean up
rm "$TEMP_FILE"
