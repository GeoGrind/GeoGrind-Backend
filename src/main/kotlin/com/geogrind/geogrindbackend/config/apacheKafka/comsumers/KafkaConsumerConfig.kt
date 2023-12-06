package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory

@Configuration
interface KafkaConsumerConfig {
    @Bean
    fun consumerFactory(): ConsumerFactory<String, String>

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String>
}