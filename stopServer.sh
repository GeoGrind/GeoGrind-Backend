#!/bin/bash

# Stop all running containers

# Load environment variables from .env file
if [ -f .env ]; then
  source .env
fi

# main server
echo "Stopping GeoGrind Server at $SERVER_HOST:$SERVER_PORT"

# database server
echo "Stopping GeoGrind Database at $DB_HOST:$DB_PORT"

# redis server
echo "Stopping GeoGrind Redis Server at $REDIS_HOST:$REDIS_PORT"

# rabbitmq server
echo "Stopping GeoGrind RabbitMQ Server at $RABBITMQ_HOST:$RABBITMQ_PORT"

docker stop $(docker ps -q)

echo "GeoGrind Server Container stopped."
echo "GeoGrind Database Container stopped."
echo "GeoGrind Redis Container stopped."
echo "GeoGrind RabbitMQ Container stopped."