package com.geogrind.geogrindbackend.utils.GrantPermissions

import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.repositories.permissions.PermissionRepository
import org.springframework.stereotype.Service

@Service
interface GrantPermissionHelper {
    fun grant_permission_helper(
        newPermissions: Set<Permissions>,
        permissionRepository: PermissionRepository,
        currentUserAccount: UserAccount
    ): Boolean
}