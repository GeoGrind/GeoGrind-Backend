package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import io.github.cdimascio.dotenv.Dotenv
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import java.util.HashMap

@Configuration
class KafkaConsumerConfigImpl : KafkaConsumerConfig {

    companion object {
        private val dotenv: Dotenv = Dotenv.configure().directory(".").load()
        private val apacheKafkaHost: String = dotenv["APACHE_KAFKA_HOST"]
        private val apacheKafkaPort: String = dotenv["APACHE_KAFKA_PORT"]
        private val log = LoggerFactory.getLogger(KafkaConsumerConfigImpl::class.java)
    }

    @Bean
    override fun consumerFactory(): ConsumerFactory<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$apacheKafkaHost:$apacheKafkaPort"
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = ""
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        log.info("Consumer topic in Kafka established!")
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    override fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }
}