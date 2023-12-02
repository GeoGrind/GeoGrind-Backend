package com.geogrind.geogrindbackend.utils.RabbitMQ

import com.geogrind.geogrindbackend.config.rabbitmq.RabbitMQConfig
import com.geogrind.geogrindbackend.config.rabbitmq.RabbitMQConfigImpl
import com.geogrind.geogrindbackend.dto.session.DeleteSessionByIdDto
import com.geogrind.geogrindbackend.models.sessions.Sessions
import com.geogrind.geogrindbackend.services.sessions.SessionService
import com.geogrind.geogrindbackend.services.sessions.SessionServiceImpl
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.BuiltinExchangeType
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.Collections

@Service
class RabbitMQHelperImpl(
    private val rabbitMQConfig: RabbitMQConfig,
    private val rabbitTemplate: RabbitTemplate,
    private val sessionService: SessionService,
) : RabbitMQHelper {
    override suspend fun sendSessionDeletionMessage(sessionToDelete: Sessions) {
        try {
            // connect to rabbitmq
            val (conn, channel) = rabbitMQConfig.connectToRabbitMQ()

            // Declare the delay exchange
            channel.exchangeDeclare(
                DELAY_EXCHANGE,
                BuiltinExchangeType.DIRECT,
                false,
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

    @RabbitListeners(
        RabbitListener(
            bindings = [QueueBinding(
                value = Queue(SessionServiceImpl.QUEUE_NAME),
                exchange = Exchange(SessionServiceImpl.DELAY_EXCHANGE),
                key = [SessionServiceImpl.ROUTING_KEY]
            )]
        )
    )
    @Caching(
        evict = [
            CacheEvict(cacheNames = ["sessions"], key = " '#requestDto.user_account_id' "),
            CacheEvict(cacheNames = ["sessions"], allEntries = true)
        ]
    )
    @Transactional
    override suspend fun handleScheduledSessionDeletion(
        @Valid requestDto: DeleteSessionByIdDto
    ) {
        log.info("Waiting for scheduled session to delete!")

        // connect to rabbitmq
        val (conn, channel) = rabbitMQConfig.connectToRabbitMQ()

        // declare the delay exchange
        channel.exchangeDeclare(
            SessionServiceImpl.DELAY_EXCHANGE,
            BuiltinExchangeType.DIRECT,
            false,
            true,
            mapOf("x-delayed-type" to "direct")
        )

        // creates a queue if it doesn't already exist
        val q = channel.queueDeclare(
            SessionServiceImpl.QUEUE_NAME,
            true,
            false,
            false,
            null,
        )

        // Bind the queue to the delay exchange. When the duration of the delay is over -> route the message to this queue for processing
        channel.queueBind(
            q.queue,
            SessionServiceImpl.DELAY_EXCHANGE,
            SessionServiceImpl.ROUTING_KEY,
        )

        // Get a message from the queue that is ready for processing
        channel.basicQos(1)

        val message: Message? = rabbitTemplate.receive(SessionServiceImpl.QUEUE_NAME)

        // Check if a message is received
        if(message != null) {
            try {
                val messagePayload = String(message.body)
                log.info("Received a message from the RabbitMQ: $messagePayload")

                // Process the payload and delete the scheduled session
                sessionService.deleteSessionById(
                    requestDto = requestDto
                )

                // acknowledge that the message has been done
                channel.basicAck(
                    message.messageProperties.deliveryTag, false)
            } catch (error: Exception) {
                log.info("Error processing message: ${String(message.body)}")
                throw RuntimeException("Error processing message: ${String(message.body)}")
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(RabbitMQHelperImpl::class.java)
        const val DELAY_EXCHANGE = "session-delay-exchange"
        const val ROUTING_KEY = "session.delete"
        const val QUEUE_NAME = "session-delete-queue"
    }
}