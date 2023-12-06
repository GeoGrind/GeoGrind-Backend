package com.geogrind.geogrindbackend.services.registration

import com.geogrind.geogrindbackend.dto.registration.CreateUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.DeleteUserAccountDto
import com.geogrind.geogrindbackend.dto.registration.GetUserAccountByIdDto
import com.geogrind.geogrindbackend.dto.registration.UpdateUserAccountDto
import jakarta.validation.Valid
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest

@ExtendWith(MockitoExtension::class)
@SpringBootTest
interface UserAccountServiceTest {

    @Test
    fun testGetAllUsers() // test get all the current users

    @Test
    fun testGetUserById(@Valid requestDto: GetUserAccountByIdDto) // test get the user based on the user id

    @Test
    fun testCreateUser(@Valid requestDto: CreateUserAccountDto) // test create new user

    @Test
    fun testUpdateUserById(@Valid requestDto: UpdateUserAccountDto) // test update the user's password

    @Test
    fun testDeleteUserById(@Valid requestDto: DeleteUserAccountDto) // test delete the user's account by the given id

}