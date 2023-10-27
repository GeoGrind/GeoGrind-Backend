package com.geogrind.geogrindbackend.config.database

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import java.util.*
import javax.sql.DataSource

@Configuration
class DatabaseConfigImpl : DatabaseConfig {

    @Bean
    override fun dataSource(): DataSource {

        // Load environment variables from the .env file
        val dotenv = Dotenv.configure().directory("/Users/kenttran/Desktop/Desktop_Folders/side_projects/GeoGrind-Backend/.env").load()

        // Fetch the environment variables
        val dbHost = dotenv["DB_HOST"]
        val dbPort = dotenv["DB_PORT"]
        val dbName = dotenv["DB_NAME"]
        val dbUserName = dotenv["DB_USERNAME"]
        val dbPassword = dotenv["DB_PASSWORD"]

        val jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"

        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.postgresql.Driver")
        dataSource.url = jdbcUrl
        dataSource.username = dbUserName
        dataSource.password = dbPassword

        return dataSource
    }
}