package com.geogrind.geogrindbackend.utils.ScheduledTask.types

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.LOCAL_VARIABLE)
@Retention(AnnotationRetention.RUNTIME)
annotation class KafkaTopicsType(val value: KafkaTopicsTypeEnum)
