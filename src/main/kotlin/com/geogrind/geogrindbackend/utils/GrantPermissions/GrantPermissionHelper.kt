package com.geogrind.geogrindbackend.utils.GrantPermissions

import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.repositories.permissions.PermissionRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import org.springframework.stereotype.Service

@Service
interface GrantPermissionHelper {
    fun grant_permission_helper(
        newPermissions: Set<Permissions>,
        currentUserAccount: UserAccount,
    ): Boolean
    fun takeArrayPermissionHelper(
        permissionToDelete: Set<PermissionName>,
        currentUserAccount: UserAccount,
    ) : Boolean
}