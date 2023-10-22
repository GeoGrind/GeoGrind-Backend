package com.geogrind.geogrindbackend.config.flyway

import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayConfigImpl : FlywayConfig {
    @Bean
    override fun flyway(dataSource: DataSource): Flyway {
        val flyway: Flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:resources/db.migration")
            .baselineOnMigrate(true) // Add this line to baseline the schema
            .load()
        flyway.migrate()
        return flyway
    }
}