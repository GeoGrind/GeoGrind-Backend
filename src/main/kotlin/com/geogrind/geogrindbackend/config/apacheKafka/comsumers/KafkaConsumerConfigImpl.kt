package com.geogrind.geogrindbackend.config.apacheKafka.comsumers

import com.geogrind.geogrindbackend.models.scheduling.KafkaTopicsTypeEnum
import com.geogrind.geogrindbackend.models.scheduling.ScheduledTaskItem
import io.github.cdimascio.dotenv.Dotenv
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction
import org.apache.flink.streaming.api.functions.windowing.RichWindowFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.util.Collector
import org.apache.kafka.clients.consumer.*
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import java.util.Properties

@Configuration
class KafkaConsumerConfigImpl : KafkaConsumerConfig {

    companion object {
        private val dotenv: Dotenv = Dotenv.configure().directory(".").load()
        private val apacheKafkaHost: String = dotenv["APACHE_KAFKA_HOST"]
        private val apacheKafkaPort: String = dotenv["APACHE_KAFKA_PORT"]
        private val log = LoggerFactory.getLogger(KafkaConsumerConfigImpl::class.java)
//        private const val topicName = KafkaTopicsTypeEnum.SESSION_DELETION_VALUE
    }

    @Bean
    override fun consumerFactory(): ConsumerFactory<String, String> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$apacheKafkaHost:$apacheKafkaPort"
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "my-group-id"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = org.apache.kafka.common.serialization.StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = org.apache.kafka.common.serialization.StringDeserializer::class.java
        log.info("Consumer topic in Kafka established!")
        return DefaultKafkaConsumerFactory(configProps)
    }

    @Bean
    override fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory()
        return factory
    }

    // Introduce Apache Kafka + Apache Flink for delay mechanism when consuming the message from the kafka stream
//    @Bean
//    override fun flinkKafkaConsumer() {
//
//        val kafkaConsumerProperties = Properties()
//        kafkaConsumerProperties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "$apacheKafkaHost:$apacheKafkaPort"
//        kafkaConsumerProperties[ConsumerConfig.GROUP_ID_CONFIG] = "my-group-id"
//        kafkaConsumerProperties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
//        kafkaConsumerProperties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
//
//        log.info("Flink consumer topic in Kafka established!")
//
//        val flinkEnv = StreamExecutionEnvironment.getExecutionEnvironment()
//
//        val flinkKafkaConsumer: DataStream<String> = flinkEnv.addSource(FlinkKafkaConsumer<String> (
//            topicName,
//            SimpleStringSchema(),
//            kafkaConsumerProperties
//        ))
//
//        val resultStream: SingleOutputStreamOperator<String> = flinkKafkaConsumer
//            .assignTimestampsAndWatermarks(object : AssignerWithPeriodicWatermarks<String> {
//                private val maxOutOfOrderness = 5000L // 5 seconds
//
//                override fun extractTimestamp(element: String?, recordTimestamp: Long): Long {
//                    return System.currentTimeMillis()
//                }
//
//                override fun getCurrentWatermark(): Watermark? {
//                    return Watermark(System.currentTimeMillis() - maxOutOfOrderness)
//                }
//            })
//
//        resultStream
//            .keyBy { "dummy-key" }
//            .window(TumblingEventTimeWindows.of(Time.seconds(10)))
//            .process(object : ProcessWindowFunction<String, ScheduledTaskItem, String, TimeWindow>() {
//                private lateinit var delayState: ValueState<Boolean>
//
//                override fun open(parameters: org.apache.flink.configuration.Configuration?) {
//                    val stateDescriptor = ValueStateDescriptor("delay-state", Boolean::class.java)
//                    delayState = runtimeContext.getState(stateDescriptor)
//                }
//
//                override fun process(
//                    key: String?,
//                    context: Context?,
//                    elements: MutableIterable<String>?,
//                    out: Collector<ScheduledTaskItem>?
//                ) {
//                    if(delayState.value() == null || !delayState.value()) {
//                        Thread.sleep(5000) // delay logic to consume the message
//                        delayState.update(true)
//                    }
//
//                    // processing message logic
//                    for (element in elements!!) {
//                        val message = messageConsumer.listen(element)
//                        log.info("Message from kafka stream: $message")
//                        if (message != null) {
//                            out!!.collect(message)
//                        }
//                    }
//                }
//
//
//            })
//
//        resultStream.print()
//        flinkEnv.execute("FlinkKafkaConsumerJob")
//    }
}