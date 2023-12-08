package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
interface MessageConsumer {
    fun listen(message: String)
}