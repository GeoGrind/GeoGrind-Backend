package com.geogrind.geogrindbackend.config.rabbitmq

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.ExchangeBuilder
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

//@Configuration
//@EnableRabbit
//class RabbitMQConfigImpl() : RabbitMQConfig {
//
//    companion object {
//        const val DELAY_EXCHANGE = "session-delay-exchange"
//        const val ROUTING_KEY = "session.delete"
//        const val QUEUE_NAME = "session-delete-queue"
//
//        // Load environment variables from the .env file
//        private val dotenv = Dotenv.configure().directory(".").load()
//        private val rabbitMQHost = dotenv["RABBITMQ_HOST"]
//        private val rabbitMQPort = dotenv["RABBITMQ_PORT"]
//    }
//
//    @Bean
//    override fun delayExchange(): DirectExchange =
//        ExchangeBuilder.directExchange(DELAY_EXCHANGE)
//            .delayed()
//            .durable(true)
//            .build()
//
//    @Bean
//    override fun queue(): org.springframework.amqp.core.Queue =
//        QueueBuilder.durable(QUEUE_NAME)
//            .build()
//
//    @Bean
//    override fun binding(): Binding =
//        BindingBuilder
//            .bind(queue())
//            .to(delayExchange())
//            .with(ROUTING_KEY)
//
//    @Bean
//    override suspend fun connectToRabbitMQ(): Pair<Connection, Channel> {
//        val factory = ConnectionFactory()
//        factory.host = rabbitMQHost
//        factory.port = rabbitMQPort.toInt()
//
//        val conn = factory.newConnection()
//        val channel = conn.createChannel()
//
//        channel.exchangeDeclare(
//            DELAY_EXCHANGE,
//            "direct",
//            true,
//            true,
//            mapOf("x-delayed-type" to "direct")
//        )
//
//        channel.queueDeclare(
//            QUEUE_NAME,
//            true,
//            false,
//            false,
//            null,
//        )
//
//        channel.queueBind(
//            QUEUE_NAME,
//            DELAY_EXCHANGE,
//            ROUTING_KEY,
//        )
//
//        return Pair<Connection, Channel>(
//            conn,
//            channel,
//        )
//    }
//}