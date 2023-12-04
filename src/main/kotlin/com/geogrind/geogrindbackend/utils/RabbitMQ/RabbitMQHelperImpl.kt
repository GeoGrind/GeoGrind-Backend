package com.geogrind.geogrindbackend.utils.RabbitMQ

import com.geogrind.geogrindbackend.config.rabbitmq.RabbitMQConfig
import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.BuiltinExchangeType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.Collections

@Service
class RabbitMQHelperImpl(
    private val rabbitMQConfig: RabbitMQConfig,
) : RabbitMQHelper {
    override suspend fun sendSessionDeletionMessage(sessionToDelete: Sessions) {
        try {
            // connect to rabbitmq
            val (conn, channel) = rabbitMQConfig.connectToRabbitMQ()

            // Declare the delay exchange
            channel.exchangeDeclare(
                DELAY_EXCHANGE,
                BuiltinExchangeType.DIRECT,
                true,
                true,
                mapOf("x-delayed-type" to "direct")
            )

            // Creates a queue if it doesn't already exist
            val q = channel.queueDeclare(
                QUEUE_NAME,
                true,
                false,
                false,
                null,
            )
            // Bind the queue to the delay exchange. When the duration of the delay is over -> route the message to this queue for processing
            channel.queueBind(
                q.queue,
                DELAY_EXCHANGE,
                ROUTING_KEY,
            )

            // Create the content of the message to be sent to RabbitMQ
            val stopTime = sessionToDelete.stopTime
            val message = "Stop session: $sessionToDelete".toByteArray(StandardCharsets.UTF_8)

            // Calculate the delay for the message
            val duration: Duration = Duration.between(stopTime, Instant.now())
            val delayInMilliseconds: Long = duration.toMillis()

            val properties = AMQP.BasicProperties.Builder()
                .deliveryMode(2)
                .headers(Collections.singletonMap("x-delay", delayInMilliseconds as Any))
                .build()

            // Publish the message to the delay exchange to hold for delayInMilliseconds
            channel.basicPublish(
                DELAY_EXCHANGE,
                ROUTING_KEY,
                true,
                false,
                properties,
                message
            )
            log.info("Message has been published to queue!")

        } catch (error: Exception) {
            log.error("Oops! An error occurred while publishing the message to the RabbitMQ queue: $error")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(RabbitMQHelperImpl::class.java)
        const val DELAY_EXCHANGE = "session-delay-exchange"
        const val ROUTING_KEY = "session.delete"
        const val QUEUE_NAME = "session-delete-queue"
    }
}