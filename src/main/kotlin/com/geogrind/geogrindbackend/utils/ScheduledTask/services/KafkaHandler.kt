package com.geogrind.geogrindbackend.utils.ScheduledTask.services

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.KafkaTopicsType

interface KafkaHandler {
    // Interface for Kafka to send message to appropriate topic
    @KafkaTopicsType(KafkaTopicsTypeEnum.DEFAULT)
    fun kafkaSendDefaultMessage(task: ScheduledTaskItem)

    @KafkaTopicsType(KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC)
    fun kafkaSendSessionDeletionMessage(task: ScheduledTaskItem)
}