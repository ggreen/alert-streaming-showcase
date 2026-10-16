#!/bin/bash

export RABBIT_USERNAME=$(cf service-key rabbitmq rabbit-key | tail -n +3 | jq -r '.credentials.username')
export RABBIT_PASSWORD=$(cf service-key rabbitmq rabbit-key | tail -n +3 | jq -r '.credentials.password')
export RABBIT_VHOST=$(cf service-key rabbitmq rabbit-key | tail -n +3 | jq -r '.credentials.vhost')
export RABBIT_URL=$(cf service-key rabbitmq rabbit-key | tail -n +3 | jq -r '.credentials.dashboard_url')
echo "RabbitMQ User $RABBIT_USERNAME , Password:$RABBIT_PASSWORD vhost: $RABBIT_VHOST"

# List of queues to remove
QUEUES=(
  "activities.activity"
  "alerts.alert"
)

# Function to URL-encode strings using pure bash
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

echo "Starting queue deletion for vhost '${RABBIT_VHOST}'..."
echo "----------------------------------------------------"

# Iterate through the list of queues
for QUEUE_NAME in "${QUEUES[@]}"; do
  ENCODED_QUEUE=$(urlencode "${QUEUE_NAME}")

  response=$(curl -s -o /dev/null -w "%{http_code}" \
    -X DELETE \
    -u "${RABBIT_USERNAME}:${RABBIT_PASSWORD}" \
    "${RABBIT_URL}/api/queues/${ENCODED_VHOST}/${ENCODED_QUEUE}")

  if [ "$response" -eq 204 ]; then
    echo "✔ Queue '${QUEUE_NAME}' deleted successfully."
  elif [ "$response" -eq 404 ]; then
    echo "✖ Error: Queue '${QUEUE_NAME}' or vhost '${RABBIT_VHOST}' not found (HTTP 404)."
  else
    echo "✖ Failed to delete queue '${QUEUE_NAME}'. HTTP status code: ${response}"
  fi
done