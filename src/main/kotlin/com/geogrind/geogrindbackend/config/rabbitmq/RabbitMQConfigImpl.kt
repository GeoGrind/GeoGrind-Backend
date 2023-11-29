package com.geogrind.geogrindbackend.config.rabbitmq

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.ExchangeBuilder
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfigImpl : RabbitMQConfig {

    companion object {
        const val DELAY_EXCHANGE = "session-delay-exchange"
        const val ROUTING_KEY = "session.delete"
        const val QUEUE_NAME = "session-delete-queue"
    }

    @Bean
    override fun delayExchange(): Exchange =
        ExchangeBuilder.directExchange(DELAY_EXCHANGE)
            .delayed()
            .durable(true)
            .build()

    @Bean
    override fun queue(): Queue =
        Queue(QUEUE_NAME)

    @Bean
    override fun binding(queue: Queue, exchange: Exchange): Binding =
        BindingBuilder.bind(queue)
            .to(delayExchange())
            .with(ROUTING_KEY)
            .noargs()
}