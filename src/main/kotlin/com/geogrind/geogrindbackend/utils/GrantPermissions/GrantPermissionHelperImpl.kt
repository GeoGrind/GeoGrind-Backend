package com.geogrind.geogrindbackend.utils.GrantPermissions

import com.geogrind.geogrindbackend.models.permissions.Permission
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.repositories.permissions.PermissionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GrantPermissionHelperImpl : GrantPermissionHelper {
    override fun grant_permission_helper(
        newPermissions: Set<Permission>,
        permissionRepository: PermissionRepository,
        userAccount: UserAccount
    ): Boolean {
        try {
            val getAllCurrentPermissions: MutableSet<Permission> = permissionRepository.findAllByFkUserAccountId(userAccount.id as UUID)
            val getAllCurrentPermissionName: MutableSet<PermissionName> = mutableSetOf()

            for(current_permission in getAllCurrentPermissions) {
                getAllCurrentPermissionName.add(current_permission.permission_name)
            }

            for(permission in newPermissions) {
                if(permission.permission_name !in getAllCurrentPermissionName) {
                    permissionRepository.save(permission)
                    userAccount.permissions.add(permission)
                }
            }

            return true
        } catch (error: RuntimeException) {
            log.info ("Error while granting permission for current user: $error")
            return false
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(GrantPermissionHelperImpl::class.java)

    }
}