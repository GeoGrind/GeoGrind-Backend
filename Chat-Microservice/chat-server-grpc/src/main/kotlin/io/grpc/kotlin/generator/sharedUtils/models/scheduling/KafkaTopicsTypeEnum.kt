package io.grpc.kotlin.generator.sharedUtils.models.scheduling

enum class KafkaTopicsTypeEnum {
    DEFAULT,
    SESSION_DELETE_TOPIC;

    companion object {
        const val SESSION_DELETION_VALUE = "SESSION_DELETE_TOPIC"
        const val DEFAULT_VALUE = "DEFAULT"
    }
}
