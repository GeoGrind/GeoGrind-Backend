package com.geogrind.geogrindbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GeoGrindBackendApplication

fun main(args: Array<String>) {
	runApplication<GeoGrindBackendApplication>(*args)
}
