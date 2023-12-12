package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener
import org.springframework.kafka.listener.ConsumerSeekAware
import org.springframework.stereotype.Component

@Component
interface MessageConsumer {
    fun listen(record: ConsumerRecord<String, String>)
}