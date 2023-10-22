package com.geogrind.geogrindbackend.config.flyway

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
interface FlywayConfig {
    @Bean
    fun flyway(@Qualifier("dataSource") dataSource: DataSource): Flyway
}