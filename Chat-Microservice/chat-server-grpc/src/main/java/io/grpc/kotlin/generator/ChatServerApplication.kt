package io.grpc.kotlin.generator

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.server.ConfigurableWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.stereotype.Component
import java.util.Collections

@SpringBootApplication
@EnableCaching
class ChatServerApplication

fun main(args: Array<String>) {
    val app = SpringApplication(ChatServerApplication::class.java)
    app.setDefaultProperties(Collections.singletonMap("server.port", "8083") as Map<String, Any>?)
    app.run(*args)
}

@Component
class ServerPostCustomizer : WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    override fun customize(factory: ConfigurableWebServerFactory?) {
        factory!!.setPort(8086)
    }
}