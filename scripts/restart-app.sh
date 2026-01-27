#!/bin/bash

APP_NAME="Lead-Submission-Observability-Lab"
PROFILE="local"

PROJECT_DIR="/web2/Lead-Submission-Observability-Lab"
STATUS_DIR="$PROJECT_DIR/logs"
STATUS_FILE="$STATUS_DIR/startup-status.log"

cd "$PROJECT_DIR" || exit 1
mkdir -p "$STATUS_DIR"

# Stop existing app
pkill -f "mvn spring-boot:run"
sleep 4

# Clear old status
> "$STATUS_FILE"

# Start app (NO LOGGING)
nohup mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE >/dev/null 2>&1 &

APP_PID=$!

# Wait for startup (max 60 seconds)
STARTED=false
for i in {1..60}; do
  if ss -lnt | grep -q ":8080"; then
    STARTED=true
    break
  fi
  sleep 2
done

# Write single status line
if [ "$STARTED" = true ]; then
  echo "$(date) : SERVER STARTED SUCCESSFULLY (PID $APP_PID)" >> "$STATUS_FILE"
else
  echo "$(date) : SERVER FAILED TO START" >> "$STATUS_FILE"
fi
