#!/bin/bash

# Load environment variables from .env file
if [ -f .env ]; then
  source .env
fi

# Check if the container with the same name already exists
if [ "$(docker ps -aq -f name=geogrind-backend)" ]; then
  # If the container exists, stop and remove it
  docker stop geogrind-backend
  docker rm geogrind-backend
fi

# main server
echo "Connecting to GeoGrind Server at $SERVER_HOST:$SERVER_PORT"

# database server
echo "Connecting to GeoGrind Database at $DB_HOST:$DB_PORT"

# redis server
echo "Connecting to GeoGrind Redis Server at $REDIS_HOST:$REDIS_PORT"

# rabbitmq server
#echo "Connecting to RabbitMQ Server at $RABBITMQ_HOST:$RABBITMQ_PORT"

# apache kafka server
echo "Connecting to Zookeeper Server at $ZOOKEEPER_HOST:$ZOOKEEPER_PORT"
echo "Connecting to Kafka Server at $APACHE_KAFKA_HOST:$APACHE_KAFKA_PORT"

# Start the Docker container
docker-compose up --build -d

echo "GeoGrind Server Container started."
echo "GeoGrind Database Container started."
echo "GeoGrind Redis Container started."
#echo "GeoGrind RabbitMQ Container started."
echo "GeoGrind Apache Kafka Container started"

# Check the logs to see if everything started successfully
docker-compose logs
