package com.geogrind.geogrindbackend.utils.ScheduledTask.proxy

import com.geogrind.geogrindbackend.config.apacheKafka.producers.MessageProducerConfig
import com.geogrind.geogrindbackend.config.apacheKafka.producers.MessageProducerConfigImpl
import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.KafkaTopicsType
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

// Invocate handler for the proxy to enqueue the task to the appropriate task queue
class KafkaTopicsProxyHandler(
    private val target: Any,
    private val task: ScheduledTaskItem,
    private val kafkaMessageProducer: MessageProducerConfigImpl,
) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        val kafkaTopicNameAnnotation = method.getAnnotation(KafkaTopicsType::class.java)
        if (kafkaTopicNameAnnotation != null) {
            val kafkaTopicType = kafkaTopicNameAnnotation.value
            // enqueue the task to the appropriate queue
            when (kafkaTopicType) {
                KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC -> {
                    kafkaMessageProducer.sendMessage(KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC, task)
                    log.info("Session deletion task has been subscribed to Kafka topic successfully!!!")
                }
                else -> {
                    kafkaMessageProducer.sendMessage(KafkaTopicsTypeEnum.DEFAULT, task)
                    log.info("Default scheduled task has been subscribed to Kafka topic successfully!!!")
                }
            }
        }

        // Default behavior if no annotation is present
        return method.invoke(target, *args.orEmpty())
    }

    companion object {
        private val log = LoggerFactory.getLogger(KafkaTopicsProxyHandler::class.java)
    }
}

// Function to create a proxy
inline fun <reified T : Any> createKafkaTopicsProxy(target: T, task: ScheduledTaskItem, kafkaMessageProducer: MessageProducerConfigImpl) : T {
    val proxyHandler = KafkaTopicsProxyHandler(target, task, kafkaMessageProducer)
    return Proxy.newProxyInstance(
        target::class.java.classLoader,
        arrayOf(T::class.java),
        proxyHandler
    ) as T
}


