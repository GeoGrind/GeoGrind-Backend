#!/bin/bash

# Stop all running containers
docker stop $(docker ps -q)