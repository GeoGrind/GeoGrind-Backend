package com.geogrind.geogrindbackend.repositories.permissions

import com.geogrind.geogrindbackend.models.permissions.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PermissionsRepository : JpaRepository<Permission, UUID> {}