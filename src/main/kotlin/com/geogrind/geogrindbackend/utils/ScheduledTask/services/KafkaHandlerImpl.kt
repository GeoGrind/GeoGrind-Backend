package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.config.apacheKafka.producers.MessageProducerConfig
import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.KafkaTopicsType
import org.slf4j.LoggerFactory

class KafkaDeleteSessionTask(
    private val kafkaMessageProducer: MessageProducerConfig
) : KafkaHandler {
    @KafkaTopicsType(KafkaTopicsTypeEnum.DEFAULT)
    override fun kafkaSendDefaultMessage(task: ScheduledTaskItem) {
        try {
            kafkaMessageProducer.sendMessage(KafkaTopicsTypeEnum.DEFAULT, task)
            log.info("Task: $task has been published successfully to Kafka topic!")
        } catch (error: Exception) {
            error.printStackTrace()
            log.error("Error while publishing task to the Kafka topic: $error")
        }
    }

    @KafkaTopicsType(KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC)
    override fun kafkaSendSessionDeletionMessage(task: ScheduledTaskItem) {
        try {
            kafkaMessageProducer.sendMessage(KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC, task)
            log.info("Task: $task has been published successfully to the Kafka topic!")
        } catch (error: Exception) {
            error.printStackTrace()
            log.error("Error while publishing task to the Kafka topic: $error")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(KafkaDeleteSessionTask::class.java)
    }
}

class KafkaDeleteDefaultTask(
    private val kafkaMessageProducer: MessageProducerConfig
) : KafkaHandler {
    @KafkaTopicsType(KafkaTopicsTypeEnum.DEFAULT)
    override fun kafkaSendDefaultMessage(task: ScheduledTaskItem) {
        try {
            kafkaMessageProducer.sendMessage(KafkaTopicsTypeEnum.DEFAULT, task)
            log.info("Task: $task has been published successfully to Kafka topic!")
        } catch (error: Exception) {
            error.printStackTrace()
            log.error("Error while publishing task to the Kafka topic: $error")
        }
    }

    @KafkaTopicsType(KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC)
    override fun kafkaSendSessionDeletionMessage(task: ScheduledTaskItem) {
        try {
            kafkaMessageProducer.sendMessage(KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC, task)
            log.info("Task: $task has been published successfully to the Kafka topic!")
        } catch (error: Exception) {
            error.printStackTrace()
            log.error("Error while publishing task to the Kafka topic: $error")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(KafkaDeleteDefaultTask::class.java)
    }
}