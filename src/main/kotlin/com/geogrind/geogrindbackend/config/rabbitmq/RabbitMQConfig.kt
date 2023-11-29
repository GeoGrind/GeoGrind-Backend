package com.geogrind.geogrindbackend.config.rabbitmq

import org.springframework.amqp.core.Binding
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
interface RabbitMQConfig {

    // delay exchange for rabbitmq
    @Bean
    fun delayExchange(): Exchange

    @Bean
    fun queue(): Queue

    @Bean
    fun binding(queue: Queue, exchange: Exchange): Binding
}