#!/bin/bash
# Script to run Redis docker image

# Load .env variables
source .env

# Check if the container with the same name already exists
if [ "$(docker ps -aq -f name=$REDIS_CONTAINER_NAME)" ]; then
  #If the container exists, stop and remove it
  docker stop $REDIS_CONTAINER_NAME
  docker rm $REDIS_CONTAINER_NAME
fi

echo "Connecting to Redis at $REDIS_HOST:$REDIS_PORT"

# Run the Redis container
docker run -d --name $REDIS_CONTAINER_NAME -p $REDIS_PORT:$REDIS_PORT -e REDIS_PASSWORD=$REDIS_PASSWORD redis:latest

echo "Redis container started."
