package com.geogrind.geogrindbackend.config.apacheKafka.producers

import io.github.cdimascio.dotenv.Dotenv
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.HashMap

@Configuration
class KafkaProducerConfigImpl : KafkaProducerConfig {

    // Load environment variables from the .env file
    companion object {
        private val dotenv: Dotenv = Dotenv.configure().directory(".").load()
        private val apacheKafkaHost: String = dotenv["APACHE_KAFKA_HOST"]
        private val apacheKafkaPort: String = dotenv["APACHE_KAFKA_PORT"]
        private val log = LoggerFactory.getLogger(KafkaProducerConfigImpl::class.java)
    }

    @Bean
    override fun producerFactory(): ProducerFactory<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$apacheKafkaHost:$apacheKafkaPort"
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        log.info("Produce topic in Kafka established!!")
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    override fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}