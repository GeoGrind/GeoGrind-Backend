package com.geogrind.geogrindbackend.repositories.permissions

import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface PermissionRepository : JpaRepository<Permissions, UUID> {
    fun findAllByUserAccount(user_account: UserAccount): MutableSet<Permissions>
}