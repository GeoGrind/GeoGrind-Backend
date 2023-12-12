# Use the official OpenJDK base image
#FROM eclipse-temurin:17-jdk-alpine
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file and .env file into the container
COPY . /app
COPY .env .env

# Install dotenv to load env variables
RUN apk update && \
    apk add --no-cache nodejs npm

# Expose the port
EXPOSE 8080

# Command to run the application with dotenv \
ENTRYPOINT ["java", "-jar", "/app/build/libs/GeoGrind-Backend-0.0.1-SNAPSHOT.jar"]
