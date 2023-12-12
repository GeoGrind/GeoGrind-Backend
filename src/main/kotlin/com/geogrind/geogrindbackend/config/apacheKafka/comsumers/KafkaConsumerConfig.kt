package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import jakarta.annotation.PostConstruct
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.scheduling.annotation.Async

@Configuration
interface KafkaConsumerConfig {
    @Bean
    fun consumerFactory(): ConsumerFactory<String, String>

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String>

//    @Bean
//    fun flinkKafkaConsumer()
}