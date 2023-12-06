package com.geogrind.geogrindbackend.config.scheduling

import com.geogrind.geogrindbackend.utils.ScheduledTask.queue.TaskSchedulerQueue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@Configuration
@EnableScheduling
class SchedulingConfigImpl(
    private val taskSchedulerQueue: TaskSchedulerQueue
) : SchedulingConfig {
    @Bean
    override fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 5
        return scheduler
    }

    @Bean
    override fun taskSchedulerQueue(taskScheduler: TaskScheduler): TaskSchedulerQueue {
        return taskSchedulerQueue(taskScheduler)
    }
}