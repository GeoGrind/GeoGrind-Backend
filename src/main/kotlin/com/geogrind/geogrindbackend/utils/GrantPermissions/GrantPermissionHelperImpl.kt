package com.geogrind.geogrindbackend.utils.GrantPermissions

import com.geogrind.geogrindbackend.models.permissions.Permissions
import com.geogrind.geogrindbackend.models.permissions.PermissionName
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.repositories.permissions.PermissionRepository
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
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
                println("Permission : $permission")
                if(permission.permission_name !in getAllCurrentPermissionName) {
                    println("Permission name: ${permission.permission_name}")
                    permissionRepository.save(permission)
                    userAccount.permissions!!.add(permission)
                    println("User Account Permissions: ${userAccount.permissions}")
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
            currentUserAccount.permissions?.removeIf { permission ->
                permissionToDelete.contains(permission.permission_name)
            }

            val permissions: MutableSet<Permissions> = permissionRepository.findAllByUserAccount(
                user_account = currentUserAccount
            )
            val filteredPermission: MutableList<UUID> = mutableListOf()
            permissions.filter { permission ->
                permissionToDelete.contains(permission.permission_name)
                filteredPermission.add(permission.permission_id!!)
            }
            log.info("Permssions to delete: $permissionToDelete")
            log.info("Filtered permission: $filteredPermission")

            filteredPermission.forEach { permissionId ->
                permissionRepository.deleteById(permissionId)
                log.info("Permission deleted successfully: $permissionId")
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