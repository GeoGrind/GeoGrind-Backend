package com.geogrind.geogrindbackend.data

import com.geogrind.geogrindbackend.models.UserAccount
import com.geogrind.geogrindbackend.repositories.UserAccountRepository
import com.geogrind.geogrindbackend.utils.GenerateAccount.GenerateRandomAccountHelperImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// this class is for loading the fake data user into the database

@Configuration
class LoadUserAccountDatabaseImpl : LoadUserAccountDatabase {

    private val log: Logger = LoggerFactory.getLogger(LoadUserAccountDatabaseImpl::class.java)

    @Bean
    override fun initDatabase(repository: UserAccountRepository): CommandLineRunner {
        return CommandLineRunner {
            val numberOfFakeAccountsToCreate: Int = 2

            for(i in 1..numberOfFakeAccountsToCreate) {
                val FakeUserAccount: UserAccount = GenerateRandomAccountHelperImpl().createFakeUserAccount()
                log.info("Preloading " + repository.save(FakeUserAccount))
            }
        }
    }
}