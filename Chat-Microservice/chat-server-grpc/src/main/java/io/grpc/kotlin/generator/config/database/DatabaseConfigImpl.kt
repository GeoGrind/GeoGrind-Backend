package io.grpc.kotlin.generator.config.database

import com.geogrind.geogrindbackend.config.database.DatabaseConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = ["io.grpc.kotlin.generator.repositories"])
@EntityScan(basePackages = ["io.grpc.kotlin.generator.models", "io.grpc.kotlin.generator.sharedUtils.models"])
class DatabaseConfigImpl : DatabaseConfig {

    @Bean
    override fun dataSource(): DataSource {

        // Load environment variables from the .env file
        val dotenv = Dotenv.configure().directory("./.").load()

        // Fetch the environment variables
        val dbHost = dotenv["DB_HOST"]
        val dbPort = dotenv["DB_PORT"]
        val dbName = dotenv["DB_NAME"]
        val dbUserName = dotenv["DB_USERNAME"]
        val dbPassword = dotenv["DB_PASSWORD"]

        val jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"

        val dataSource = HikariDataSource()
        dataSource.jdbcUrl = jdbcUrl
        dataSource.username = dbUserName
        dataSource.password = dbPassword

        // Customize connection pool properties
        dataSource.maximumPoolSize = 10
        dataSource.minimumIdle = 2
        dataSource.idleTimeout = 10000 // 10 seconds
        dataSource.maxLifetime = 1800000 // 30 minutes

        return dataSource
    }
}