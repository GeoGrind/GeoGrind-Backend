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

# RabbitMQ Container
FROM rabbitmq:3.10-management

RUN apt-get update && \
apt-get install -y curl unzip

RUN curl -L https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/3.10.2/rabbitmq_delayed_message_exchange-3.10.2.ez > rabbitmq_delayed_message_exchange-3.10.2.ez && \
mv rabbitmq_delayed_message_exchange-3.10.2.ez plugins/

RUN rabbitmq-plugins enable rabbitmq_delayed_message_exchange

