package io.grpc.kotlin.generator

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.kotlin.generator.services.chatroom.gRPC.ChatRoomGrpcServiceApplication
import io.grpc.kotlin.generator.services.chatroom.spring.ChatRoomService
import io.grpc.kotlin.generator.utils.gRPC2Spring.Grpc2SpringConversion
import io.grpc.kotlin.generator.utils.gRPC2Spring.Grpc2SpringConversionImpl
import io.grpc.kotlin.generator.utils.gRPC2Spring.Spring2GrpcConversion
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.util.concurrent.TimeUnit

@Slf4j
class ChatGrpcServerApplication (
    private val port: Int,
    private val springService: ChatRoomService,
    private val spring2GrpcConversion: Spring2GrpcConversion,
    private val gRPC2SpringConversion: Grpc2SpringConversion
) {
    private val server: Server

    init {
        val chatRoomAPI = ChatRoomGrpcServiceApplication(
            springService = springService,
            spring2GrpcConversion = spring2GrpcConversion,
            gRPC2SpringConversion = gRPC2SpringConversion,
        )
        server = ServerBuilder.forPort(port)
            .addService(chatRoomAPI).build()
    }

    fun start() {
        log.info("Starting chat gRPC Server ..")
        server.start()
        log.info("Server started on port {} ", port)
        Runtime.getRuntime()
            .addShutdownHook(
                Thread {
                    try {
                        this.stop()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            )
    }

    private fun stop() {
        log.info("Stopping gRPC chat Server ..")
        server?.let {
            it.shutdown().awaitTermination(30, TimeUnit.SECONDS)
        }
    }

    private fun blockUntilShutDown() {
        server?.let {
            it.awaitTermination()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ChatGrpcServerApplication::class.java)
        @JvmStatic
        fun main(args: Array<String>) {
            val applicationContext = AnnotationConfigApplicationContext(ChatSpringBootServerApplication::class.java)
            val chatRoomService = applicationContext.getBean(ChatRoomService::class.java)

            val allBeans = applicationContext.getBeansOfType(Any::class.java)
            for ((beanName, bean) in allBeans) {
                println("Bean Name: $beanName, Bean Type: ${bean.javaClass.name}")
            }

            val spring2GrpcConversion = applicationContext.getBean(Spring2GrpcConversion::class.java)
            val gRPC2SpringConversion = applicationContext.getBean(Grpc2SpringConversion::class.java)

            val gRPCServer = ChatGrpcServerApplication(
                port = 3000,
                springService = chatRoomService,
                spring2GrpcConversion = spring2GrpcConversion,
                gRPC2SpringConversion = gRPC2SpringConversion,
            )
            gRPCServer.start()
            gRPCServer.blockUntilShutDown()
        }
    }
}