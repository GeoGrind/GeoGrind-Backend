package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
interface MessageConsumer {
    @KafkaListener(topics = ["my-topics"], groupId = "my-group-id")
    fun listen(message: String)
}