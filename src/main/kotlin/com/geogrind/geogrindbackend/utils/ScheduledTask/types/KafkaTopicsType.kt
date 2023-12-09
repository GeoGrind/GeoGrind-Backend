package com.geogrind.geogrindbackend.utils.ScheduledTask.types

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.EXPRESSION, AnnotationTarget.LOCAL_VARIABLE)
@Retention(AnnotationRetention.SOURCE)
annotation class KafkaTopicsType(val value: KafkaTopicsTypeEnum)
