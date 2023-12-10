package com.geogrind.geogrindbackend.config.apacheKafka.producers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MessageProducerConfigImpl : MessageProducerConfig {
    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    override fun sendMessage(topic: KafkaTopicsTypeEnum, task: ScheduledTaskItem) {
        try {
            kafkaTemplate.send(topic.toString(), task.toString())
            log.info("Sending task={} to kafka topic={} successfully!", topic.toString(), task.toString())

            // Introduce Apache Flink + Kafka to process with a delay

        } catch (err: Exception) {
            log.error("Error while sending task to kafka topic: $err")
        }
    }

    private fun processMessageDelay() {
        // Set up
    }

    companion object {
        private val log = LoggerFactory.getLogger(MessageProducerConfigImpl::class.java)
    }
}