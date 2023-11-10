package com.geogrind.geogrindbackend.repositories.permissions

import com.geogrind.geogrindbackend.models.permissions.Permissions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface PermissionRepository : JpaRepository<Permissions, UUID> {
    fun findAllByFkUserAccountId(user_id: UUID): MutableSet<Permissions>
}