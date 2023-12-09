package com.geogrind.geogrindbackend.utils.ScheduledTask.proxy

import com.geogrind.geogrindbackend.config.apacheKafka.producers.MessageProducerConfig
import com.geogrind.geogrindbackend.config.apacheKafka.producers.MessageProducerConfigImpl
import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.KafkaDeleteDefaultTask
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.KafkaDeleteSessionTask
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.KafkaHandler
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.KafkaTopicsType
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

// Invocate handler for the proxy to enqueue the task to the appropriate task queue
class KafkaTopicsProxyHandler(
    private val kafkaTopicType: Class<out KafkaHandler>,
    private val task: ScheduledTaskItem,
    private val kafkaMessageProducer: MessageProducerConfigImpl,
) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method, args: Array<out Any>?): Any {
        val kafkaTopicNameAnnotation = method.getAnnotation(KafkaTopicsType::class.java)
        log.info("I have been invoked!!!!!!!")
        if (kafkaTopicNameAnnotation != null) {
            try {
                log.info("Pushing the message to the kafka topic!!")
                val kafkaTopicType = kafkaTopicNameAnnotation.value
                // enqueue the task to the appropriate queue
                return KafkaFactory.publishMessage(
                    kafkaTopicType,
                    kafkaMessageProducer,
                    task,
                )
            } catch (error: Exception) {
                error.printStackTrace()
                log.error("Received a task to be published to the Apache Kafka Stream, but failed due to: $error")
            }
        }
        // Default behavior if no annotation is present
        return method.invoke(kafkaTopicType.getDeclaredConstructor().newInstance(), *args.orEmpty())
    }

    companion object {
        private val log = LoggerFactory.getLogger(KafkaTopicsProxyHandler::class.java)
    }
}

object KafkaFactory {
    fun publishMessage(kafkaTopicType: KafkaTopicsTypeEnum, kafkaMessageProducer: MessageProducerConfig, task: ScheduledTaskItem) {
        return when(kafkaTopicType) {
            KafkaTopicsTypeEnum.DEFAULT -> {
                KafkaDeleteDefaultTask(kafkaMessageProducer)
                    .kafkaSendDefaultMessage(task)
            }
            KafkaTopicsTypeEnum.SESSION_DELETE_TOPIC -> {
                KafkaDeleteSessionTask(kafkaMessageProducer)
                    .kafkaSendSessionDeletionMessage(task)
            }
        }
    }

    // Function to create a proxy
    inline fun <reified T : KafkaHandler> createKafkaTopicsProxy(task: ScheduledTaskItem, kafkaMessageProducer: MessageProducerConfigImpl) : T {
        val proxyHandler = KafkaTopicsProxyHandler(T::class.java, task, kafkaMessageProducer)
        return Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
            proxyHandler
        ) as T
    }
}




