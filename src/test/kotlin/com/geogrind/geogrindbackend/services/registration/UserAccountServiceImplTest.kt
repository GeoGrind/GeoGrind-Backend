package com.geogrind.geogrindbackend.services.registration

import com.geogrind.geogrindbackend.dto.registration.CreateUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.DeleteUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.GetUserAccountByIdDto
import com.geogrind.geogrindbackend.dto.registration.UpdateUserAccountDto
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import java.text.SimpleDateFormat
import java.util.*

class UserAccountServiceImplTest : UserAccountServiceTest {

    @InjectMocks
    private lateinit var userAccountService : UserAccountServiceImpl

    @Mock
    private lateinit var userAccountRepository: UserAccountRepository

    @Test
    override fun testGetAllUsers() {
        // Define a list of user accounts as test data
        val dateString_1: String = "2023-10-21T16:52:17.619+00:00"
        val dateString_2: String = "2023-10-21T16:52:18.758+00:00"
        val dateString_3: String = "2023-10-22T02:52:08.932+00:00"

        // Define the date format based on the provided string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

        // Parse the string and convert it to a Date object
        val date_1 = dateFormat.parse(dateString_1)
        val date_2 = dateFormat.parse(dateString_2)
        val date_3 = dateFormat.parse(dateString_3)

        val userAccounts = listOf<UserAccount>(
            UserAccount(
                id = UUID.fromString("b7dca91f-18c5-4e24-bd31-9262bb690051"),
                email = "KUUHDSj2de@example.com",
                username = "uqUyPrft",
                hashed_password = "$2a$12$4R/.Kia5Grco0r6hkXSPHuuzQWRsN9WJ.ZoJOWNWbWuDHNFqaSL3a",
                account_verified = false,
                temp_token = null,
                createdAt = date_1,
                updatedAt = date_1,
            ),
            UserAccount(
                id = UUID.fromString("7d100f9f-fa3c-4a93-9e1e-1712bac57887"),
                email = "xBX36DgO9o@example.com",
                username = "QlgiTwV7",
                hashed_password = "$2a$12$wb65JjkXQ0TJGlXnVXCmPOmMf1cNe9dPrtS23HNyU7fhes3jtPj/a",
                account_verified = false,
                temp_token = null,
                createdAt = date_2,
                updatedAt = date_2,
            ),
            UserAccount(
                id = UUID.fromString("85a647ce-50d7-4ad0-b408-72ecf4684e18"),
                email = "donewov@gmail.com",
                username = "Your mom",
                hashed_password = "$2a$12$/Hws29/rwX.ZXb.LK0uKT.BaJNKMICQzKOBBvW.Wt0djg.9CHg5pe",
                account_verified = false,
                temp_token = null,
                createdAt = date_3,
                updatedAt = date_3
            )
        )
    }

    @Test
    override fun testGetUserById(requestDto: GetUserAccountByIdDto) {
        TODO("Not yet implemented")
    }

    @Test
    override fun testCreateUser(requestDto: CreateUserAccountDto) {
        TODO("Not yet implemented")
    }

    @Test
    override fun testUpdateUserById(requestDto: UpdateUserAccountDto) {
        TODO("Not yet implemented")
    }

    @Test
    override fun testDeleteUserById(requestDto: DeleteUserAccountDto) {
        TODO("Not yet implemented")
    }
}