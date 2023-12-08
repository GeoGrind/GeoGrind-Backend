package com.geogrind.geogrindbackend.config.scheduling

import com.geogrind.geogrindbackend.utils.ScheduledTask.queue.TaskSchedulerQueue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
interface SchedulingConfig {
    @Bean
    fun taskScheduler(): TaskScheduler

//    @Bean
//    fun taskSchedulerQueue(taskScheduler: TaskScheduler): TaskSchedulerQueue
}