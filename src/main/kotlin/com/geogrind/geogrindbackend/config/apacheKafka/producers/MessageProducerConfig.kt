package com.geogrind.geogrindbackend.config.apacheKafka.producers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import org.springframework.stereotype.Component

@Component
interface MessageProducerConfig {
    fun sendMessage(topic: KafkaTopicsTypeEnum, task: ScheduledTaskItem)
}