package com.geogrind.geogrindbackend.services.registration

import com.geogrind.geogrindbackend.dto.registration.CreateUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.DeleteUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.GetUserAccountByIdDto
import com.geogrind.geogrindbackend.dto.registration.UpdateUserAccountDto
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock

class UserAccountServiceImplTest : UserAccountServiceTest {

    @InjectMocks
    private lateinit var userAccountService : UserAccountServiceImpl

    @Mock
    private lateinit var userAccountRepository: UserAccountRepository

    @Test
    override fun testGetAllUsers() {
        TODO("Not yet implemented")
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