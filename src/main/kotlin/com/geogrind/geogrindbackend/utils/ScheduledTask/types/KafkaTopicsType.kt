package com.geogrind.geogrindbackend.utils.ScheduledTask.types

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class KafkaTopicsType(val value: KafkaTopicsTypeEnum)
