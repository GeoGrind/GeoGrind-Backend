package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class MessageConsumerImpl : MessageConsumer {
    companion object {
        private const val topicName = KafkaTopicsTypeEnum.SESSION_DELETION_VALUE
    }

    @KafkaListener(topics = [topicName], groupId = "my-group-id")
    override fun listen(message: String) {
        println("Received message: $message")
    }
}