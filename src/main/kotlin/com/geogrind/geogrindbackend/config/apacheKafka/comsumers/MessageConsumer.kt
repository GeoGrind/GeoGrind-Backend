package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
interface MessageConsumer {
    fun listen(task: String)
}