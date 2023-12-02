package com.geogrind.geogrindbackend

import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
@EnableRabbit
class GeoGrindBackendApplication

fun main(args: Array<String>) {
	runApplication<GeoGrindBackendApplication>(*args)
}
