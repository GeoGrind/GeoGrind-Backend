package com.geogrind.geogrindbackend.config.rabbitmq

import com.geogrind.geogrindbackend.models.sessions.Sessions
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

// indicate type of receiver for the Message Broke
@Component
interface RabbitMQReceiver {
    fun receiveTask(task: Sessions)

    fun receiveMessage(message: String)
    fun getLatch(): CountDownLatch
}