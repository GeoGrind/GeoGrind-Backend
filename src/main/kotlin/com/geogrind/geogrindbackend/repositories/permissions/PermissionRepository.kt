package com.geogrind.geogrindbackend.repositories.permissions

import com.geogrind.geogrindbackend.models.permissions.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface PermissionRepository : JpaRepository<Permission, UUID> {
    fun findAllByFkUserAccountId(user_id: UUID): MutableSet<Permission>
}