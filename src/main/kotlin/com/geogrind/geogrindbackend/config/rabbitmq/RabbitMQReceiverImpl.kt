package com.geogrind.geogrindbackend.config.rabbitmq

import com.geogrind.geogrindbackend.models.sessions.Sessions
import org.slf4j.LoggerFactory
import java.util.concurrent.CountDownLatch

//class RabbitMQReceiverImpl : RabbitMQReceiver {
//    private val latch = CountDownLatch(1)
//
//    override fun receiveTask(task: Sessions) {
//        log.info("Scheduling task received <$task>")
//        latch.countDown()
//    }
//
//    override fun receiveMessage(message: String) {
//        log.info("Received <$message>")
//        latch.countDown()
//    }
//
//    override fun getLatch(): CountDownLatch {
//        return latch
//    }
//
//    companion object {
//        private val log = LoggerFactory.getLogger(RabbitMQConfigImpl::class.java)
//    }
//}