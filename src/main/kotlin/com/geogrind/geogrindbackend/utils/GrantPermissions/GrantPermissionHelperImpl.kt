package com.geogrind.geogrindbackend.utils.GrantPermissions

import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.repositories.permissions.PermissionRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.utils.Cookies.CreateTokenCookie
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GrantPermissionHelperImpl(
    private val permissionRepository: PermissionRepository,
    private val userAccountRepository: UserAccountRepository,
) : GrantPermissionHelper {
    override fun grant_permission_helper(
        newPermissions: Set<Permissions>,
        userAccount: UserAccount,
    ): Boolean {
        try {
            val getAllCurrentPermissions: MutableSet<Permissions> = permissionRepository.findAllByUserAccount(userAccount)
            val getAllCurrentPermissionName: MutableSet<PermissionName> = mutableSetOf()

            for(current_permission in getAllCurrentPermissions) {
                getAllCurrentPermissionName.add(current_permission.permission_name)
            }

            for(permission in newPermissions) {
                if(permission.permission_name !in getAllCurrentPermissionName) {
                    permissionRepository.save(permission)
                    userAccount.permissions!!.add(permission)
                }
            }

            userAccountRepository.save(userAccount)

            return true
        } catch (error: RuntimeException) {
            log.info ("Error while granting permission for current user: $error")
            return false
        }
    }

    override fun takeAwayPermissionHelper(
        permissionToDelete: Set<PermissionName>,
        currentUserAccount: UserAccount,
    ) : Boolean {
        try {
            // delete the permissions from the "one" side
            val permissions: MutableSet<Permissions> = permissionRepository.findAllByUserAccount(
                user_account = currentUserAccount
            )

            val filteredPermissions = permissions.filter { permission ->
                permission.permission_name in permissionToDelete
            }

            log.info("Filtered permission: $filteredPermissions")

            filteredPermissions.forEach { permission ->
                permissionRepository.deleteById(permission.permission_id!!)
            }

            // delete the permission from the "many" side
            currentUserAccount.permissions?.removeIf { permission ->
                permission.permission_name in permissionToDelete
            }

            currentUserAccount.permissions?.forEach { permissions ->
                log.info("Current permission in user account: ${permissions.permission_name}")
            }
            userAccountRepository.save(currentUserAccount)

            return true
        } catch (error: RuntimeException) {
            log.info ("Error while taking away permissions from current user: $error")
            return false
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(GrantPermissionHelperImpl::class.java)

    }
}