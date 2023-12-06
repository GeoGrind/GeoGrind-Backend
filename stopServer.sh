#!/bin/bash

# Stop all running containers

# main server
echo "Stopping GeoGrind Server at $SERVER_HOST:$SERVER_PORT"

# database server
echo "Stopping GeoGrind Database at $DB_HOST:$DB_PORT"

# redis server
echo "Stopping GeoGrind Redis Server at $REDIS_HOST:$REDIS_PORT"

docker stop $(docker ps -q)

echo "GeoGrind Server Container stopped."
echo "GeoGrind Database Container stopped."
echo "GeoGrind Redis Container stopped."