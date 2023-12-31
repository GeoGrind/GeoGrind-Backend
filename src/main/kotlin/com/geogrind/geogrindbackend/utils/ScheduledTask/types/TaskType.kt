package com.geogrind.geogrindbackend.utils.ScheduledTask.types

import com.geogrind.geogrindbackend.models.scheduling.TaskTypeEnum

// Custom annotation class
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.LOCAL_VARIABLE)
@Retention(AnnotationRetention.RUNTIME)
annotation class TaskType(val value: TaskTypeEnum)
