#!/bin/bash

# Configuration
QUEUE_NAME="activities.activity"

# Function to URL-encode strings using pure bash (handles special characters like '/')
urlencode() {
  local string="${1}"
  local length="${#string}"
  local i char
  for (( i = 0; i < length; i++ )); do
    char="${string:i:1}"
    case "${char}" in
      [a-zA-Z0-9.~_-]) printf "%s" "${char}" ;;
      *) printf "%%%02X" "'${char}" ;;
    esac
  done
}

ENCODED_VHOST=$(urlencode "${RABBIT_VHOST}")

# Delete the queue using RabbitMQ Management HTTP API
response=$(curl -s -o /dev/null -w "%{http_code}" \
  -X DELETE \
  -u "${RABBIT_USERNAME}:${RABBIT_PASSWORD}" \
  "${RABBIT_URL}/api/queues/${RABBIT_VHOST}/${QUEUE_NAME}")

if [ "$response" -eq 204 ]; then
  echo "Queue '${QUEUE_NAME}' in vhost '${RABBIT_VHOST}' deleted successfully."
elif [ "$response" -eq 404 ]; then
  echo "Error: Queue '${QUEUE_NAME}' or vhost '${VHOST}' not found (HTTP 404)."
else
  echo "Failed to delete queue. HTTP response status code: ${response}"
fi