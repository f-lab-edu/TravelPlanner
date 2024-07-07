#!/bin/bash

LOG_FILE="/var/lib/docker/volumes/travelplanner_spring-boot-logs/_data/roamgram.log"
DESTINATION="localhost:5044"
TIMESTAMP_FILE="/tmp/roamgram_last_timestamp"
TEMP_FILE=$(mktemp)

# Function to extract timestamp from a log entry
extract_timestamp() {
    echo "$1" | sed -n 's/.*"timestamp":"\([^"]*\)".*/\1/p'
}

# Copy the log file to a temporary location
cp "$LOG_FILE" "$TEMP_FILE"

# Get the last timestamp
if [ -f "$TIMESTAMP_FILE" ]; then
    LAST_TIMESTAMP=$(cat "$TIMESTAMP_FILE")
else
    LAST_TIMESTAMP=""
fi

# Extract new log entries
NEW_LOGS=""
while IFS= read -r log_entry; do
    LOG_TIMESTAMP=$(extract_timestamp "$log_entry")
    if [[ -z "$LOG_TIMESTAMP" ]]; then
        continue
    fi
    if [[ -z "$LAST_TIMESTAMP" || "$LOG_TIMESTAMP" > "$LAST_TIMESTAMP" ]]; then
        NEW_LOGS="$NEW_LOGS$log_entry"$'\n'
    fi
done < "$TEMP_FILE"

# Send new log entries if any
if [ -n "$NEW_LOGS" ]; then
    echo "$NEW_LOGS" | nc -q0 $(echo $DESTINATION | tr ':' ' ')

    # Update the last timestamp only if new logs were sent
    LAST_TIMESTAMP=$(extract_timestamp "$(tail -n 1 <<< "$NEW_LOGS")")
    echo "$LAST_TIMESTAMP" > "$TIMESTAMP_FILE"
fi

# Clean up
rm "$TEMP_FILE"
