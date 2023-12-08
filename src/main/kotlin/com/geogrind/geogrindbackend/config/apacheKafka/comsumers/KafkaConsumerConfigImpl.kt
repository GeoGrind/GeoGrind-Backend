package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import io.github.cdimascio.dotenv.Dotenv
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.*
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
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
    override fun createKafkaConsumer(): Consumer<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$apacheKafkaHost:$apacheKafkaPort"
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "my-group-id"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = org.apache.kafka.common.serialization.StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = org.apache.kafka.common.serialization.StringDeserializer::class.java
        log.info("Consumer topic in Kafka established!")
        return KafkaConsumer(configProps)
    }

    @Bean
    override fun configureAndListenToKafkaTopics() {
        val consumer = createKafkaConsumer()

        // Subscribe to all the Kafka topics
        consumer.subscribe(listOf(KafkaTopicsTypeEnum.DEFAULT.toString(), KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC.toString()))

        // Poll for messages
        while (true) {
            val records: ConsumerRecords<String, String>? = consumer.poll(Duration.ofMillis(100))
            records?.forEach { consumerRecord: ConsumerRecord<String, String>? ->
                log.info("Received message: ${consumerRecord?.value()} from topic: ${consumerRecord?.topic()}")
                /*
                * Processing the task from the topic needs to be implemented
                * */
            }
        }
    }

    @PostConstruct
    override fun startKafkaListener() {
        // Start a seperate thread to listen to Kafka topics
        Thread {
            configureAndListenToKafkaTopics()
        }.start()
    }
}