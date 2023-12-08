package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory

@Configuration
interface KafkaConsumerConfig {
    @Bean
    fun createKafkaConsumer(): Consumer<String, String>

    @Bean
    fun configureAndListenToKafkaTopics()

    @PostConstruct
    fun startKafkaListener()
}