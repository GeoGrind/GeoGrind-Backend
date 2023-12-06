package com.geogrind.geogrindbackend.config.database

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import javax.sql.DataSource

@Configuration
interface DatabaseConfig {
    @Bean
    fun dataSource(): DataSource
}