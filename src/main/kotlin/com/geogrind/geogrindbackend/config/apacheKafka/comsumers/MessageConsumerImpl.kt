package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import com.geogrind.geogrindbackend.models.scheduling.Task
import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum
import com.geogrind.geogrindbackend.repositories.sessions.SessionsRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import com.geogrind.geogrindbackend.services.sessions.SessionService
import com.geogrind.geogrindbackend.utils.ScheduledTask.proxy.TaskExecutedFactory
import com.geogrind.geogrindbackend.utils.ScheduledTask.services.TaskExecutedHandler
import com.geogrind.geogrindbackend.utils.ScheduledTask.types.TaskType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.hibernate.Session
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class MessageConsumerImpl(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val userAccountRepository: UserAccountRepository,
    private val userProfileRepository: UserProfileRepository,
    private val sessionsRepository: SessionsRepository,
    private val sessionService: SessionService,
) : MessageConsumer {
    companion object {
        private const val topicName = KafkaTopicsTypeEnum.SESSION_DELETION_VALUE
        private const val retryTopicName = "retry-topic"
        private const val deadLetterTopicName = "dead-letter-topic"
        private val log = LoggerFactory.getLogger(MessageConsumerImpl::class.java)
        private fun waitSomeTime(duration: Long) {
            log.info("Starting waiting for scheduled task begin!!")
            try {
                Thread.sleep(duration)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            log.info("Scheduled task is now executed!!")
        }
    }

//    private val retryCountMap: MutableMap<TopicPartition, MutableMap<String, AtomicInteger>> = ConcurrentHashMap()

    @KafkaListener(
        topics = [topicName],
        groupId = "my-group-id",
    )
//    @OptIn(ExperimentalSerializationApi::class)
//    @Retryable(
//        value = [Exception::class],
//        maxAttemptsExpression = "\${kafka.consumer.retry.max-attempts}",
//        backoff = Backoff(
//            delayExpression = "\${kafka.consumer.retry.initial-delay}",
//            maxDelayExpression = "\${kafka.consumer.retry.max-delay}",
//            multiplierExpression = "\${kafka.consumer.retry.multiplier}"
//        )
//    )
    override fun listen(record: ConsumerRecord<String, String>) {
        try {
            val task = record.value()
            log.info("Received task: $task")

            // Extracting values using regex

            val matchResult = extractValues(task)

            if (matchResult != null) {
                val scheduledTaskItem = createScheduledTaskItem(matchResult)

                log.info("Scheduled Task Item object: $scheduledTaskItem")

                // delay mechanism for scheduled task
                val duration: Duration = Duration.between(LocalDateTime.now(), scheduledTaskItem.executionTime)
                val timeDelay: Long = duration.toMillis()

                // Start a seperate thread for countdown logging
                Executors.newSingleThreadExecutor().submit {
                    logCountdown(timeDelay, scheduledTaskItem.taskId)
                }

                waitSomeTime(timeDelay)

                // proceed to execute the schedule task
                val taskExecutionProxy = TaskExecutedFactory.createTaskExecutedProxy<TaskExecutedHandler>(
                    task = scheduledTaskItem,
                    userAccountRepository = userAccountRepository,
                    userProfileRepository = userProfileRepository,
                    sessionsRepository = sessionsRepository,
                    sessionService = sessionService,
                )

                scheduledTaskItem.scheduledTask?.sessionId?.let {
                    // if the session id is presented -> the task is delete the session
                    taskExecutionProxy.sessionDeletionTaskExecuted(
                        task = scheduledTaskItem
                    )
                }

                // Simulating an exception for demonstration purposes
                if(record.value().contains("simulate-error")) {
                    throw RuntimeException("Simulated error")
                }
                log.info("Processing completed successfully!!")
            } else {
                log.info("String format does not match expected pattern.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            log.error("Exception while processing Kafka record: $e. Sending to retry topic.")
        }
    }

    private fun extractValues(task: String): MatchResult? {
        val regex =
            Regex("""ScheduledTaskItem\(taskId=(.*), scheduledTask=Task\(sessionId=(.*)\), executionTime=(.*), dependencies=(.*), priority=(.*)\)""")
        return regex.find(task)
    }

    private fun createScheduledTaskItem(matchResult: MatchResult): ScheduledTaskItem {
        val (taskId, sessionID, executionTime, dependencies, priority) = matchResult.destructured
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
        val localDateTimeExecutionTime = LocalDateTime.parse(executionTime, formatter)

        return ScheduledTaskItem(
            taskId = UUID.fromString(taskId),
            scheduledTask = Task(sessionId = UUID.fromString(sessionID)),
            executionTime = localDateTimeExecutionTime,
            dependencies = dependencies.split(',').map { it.trim() }.toSet(),
            priority = priority.toInt()
        )
    }

    private fun logCountdown(timeDelay: Long, taskId: UUID) {
        var countdown = TimeUnit.MILLISECONDS.toSeconds(timeDelay)
        while (countdown > 0) {
            log.info("Task with task id: $taskId is waiting to be executed in: $countdown seconds")
            countdown--
            try {
                TimeUnit.SECONDS.sleep(1)
            } catch (e: InterruptedException) {
                log.info("Error caught: $e, interrupt the countdown thread now!")
                Thread.currentThread().interrupt()
            }
        }
    }

//    @Recover
//    fun handleRecovery(e: Exception, @Payload message: String?) {
//
//    }
//
//    private fun sendToDeadLetterQueue(message: String?, exception: Exception) {
//        kafkaTemplate.send("dead-letter-topic", message)
//        log.info("Message sent to dead letter queue: $message")
//    }
//
//    override fun onPartitionsAssigned(partitions: MutableCollection<org.apache.kafka.common.TopicPartition>) {
//        super.onPartitionsAssigned(partitions)
//    }
//
//    override fun onPartitionsRevokedBeforeCommit(
//        consumer: Consumer<*, *>,
//        partitions: MutableCollection<org.apache.kafka.common.TopicPartition>,
//        callback: ConsumerSeekCallback
//    ) {
//        super.onPartitionsRevokedBeforeCommit(consumer, partitions)
//    }
}