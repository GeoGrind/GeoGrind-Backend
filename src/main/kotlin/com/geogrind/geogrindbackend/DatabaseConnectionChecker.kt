package com.geogrind.geogrindbackend

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

// Check if the connection between the server and the cloud storage is established

@Component
class DatabaseConnectionChecker @Autowired constructor(private val jdbcTemplate: JdbcTemplate) : CommandLineRunner {

    override fun run(vararg  args: String?) {
        try {
            val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)

            if(result == 1) {
                println("Database connection established.")
            } else {
                println("Database connection check failed.")
            }
        } catch (e: Exception) {
            println("Database connection check failed: ${e.message}")
        }
    }
}