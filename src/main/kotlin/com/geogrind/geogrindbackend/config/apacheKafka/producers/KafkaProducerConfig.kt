package com.geogrind.geogrindbackend.config.apacheKafka.producers

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

// create a producer for kafka topic
interface KafkaProducerConfig {
    @Bean
    fun producerFactory(): ProducerFactory<String, String>

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String>
}