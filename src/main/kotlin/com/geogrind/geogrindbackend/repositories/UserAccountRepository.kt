package com.geogrind.geogrindbackend.repositories

import com.geogrind.geogrindbackend.models.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserAccountRepository : JpaRepository<UserAccount, UUID> {}