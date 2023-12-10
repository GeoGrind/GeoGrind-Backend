package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.Task
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.decodeFromString
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class MessageConsumerImpl : MessageConsumer {
    companion object {
        private const val topicName = KafkaTopicsTypeEnum.SESSION_DELETION_VALUE
        private val log = LoggerFactory.getLogger(MessageConsumerImpl::class.java)
    }

    @KafkaListener(topics = [topicName], groupId = "my-group-id")
    @OptIn(ExperimentalSerializationApi::class)
    override fun listen(task: String) {
        try {
            log.info("Received task: $task")

            // Extracting values using regex
            val regex = Regex("""ScheduledTaskItem\(taskId=(.*), scheduledTask=Task\(sessionId=(.*)\), executionTime=(.*), dependencies=(.*), priority=(.*)\)""")
            val matchResult = regex.find(task)

            if (matchResult != null) {
                val (taskId, sessionID, executionTime, dependencies, priority) = matchResult.destructured
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
                val localDateTimeExecutionTime = LocalDateTime.parse(executionTime, formatter)
                val scheduledTaskItem = ScheduledTaskItem(
                    taskId = UUID.fromString(taskId),
                    scheduledTask = Task(sessionId = UUID.fromString(sessionID)),
                    executionTime = localDateTimeExecutionTime,
                    dependencies = dependencies.split(',').map { it.trim() }.toSet(),
                    priority = priority.toInt()
                )

                log.info("Scheduled Task Item object: $scheduledTaskItem")
            } else {
                log.info("String format does not match expected pattern.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            log.error("Exception while processing Kafka record: ", e)
        }
    }
}