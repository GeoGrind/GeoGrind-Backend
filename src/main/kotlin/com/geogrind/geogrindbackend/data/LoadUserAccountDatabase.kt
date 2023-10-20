package com.geogrind.geogrindbackend.data

import com.geogrind.geogrindbackend.repository.UserAccountRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
interface LoadUserAccountDatabase {
    @Bean
    fun initDatabase(repository: UserAccountRepository): CommandLineRunner
}