package com.geogrind.geogrindbackend.config.rabbitmq
//
//import com.rabbitmq.client.Channel
//import com.rabbitmq.client.Connection
//import org.springframework.amqp.core.Binding
//import org.springframework.amqp.core.BindingBuilder
//import org.springframework.amqp.core.CustomExchange
//import org.springframework.amqp.core.DirectExchange
//import org.springframework.amqp.core.TopicExchange
//import org.springframework.amqp.rabbit.annotation.Exchange
//import org.springframework.amqp.rabbit.annotation.Queue
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//
//@Configuration
//interface RabbitMQConfig {
//
//    // delay exchange for rabbitmq
//    @Bean
//    fun delayExchange(): DirectExchange
//
//    @Bean
//    fun queue(): org.springframework.amqp.core.Queue
//
//    @Bean
//    fun binding(): Binding
//
//    @Bean
//    suspend fun connectToRabbitMQ(): Pair<Connection, Channel>
//}