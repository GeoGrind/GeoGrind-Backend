package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class MessageConsumerImpl : MessageConsumer {
    @KafkaListener(topics = ["my-topic"], groupId = "my-group-id")
    override fun listen(message: String) {
        println("Received message: $message")
    }
}