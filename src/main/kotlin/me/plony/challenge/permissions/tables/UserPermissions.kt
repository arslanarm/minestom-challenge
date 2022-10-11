package me.plony.challenge.permissions.tables

import net.minestom.server.MinecraftServer
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.and

object UserPermissions : LongIdTable() {
    val user = reference("user", Users, onDelete = ReferenceOption.CASCADE)
    val permission = reference("permission", Permissions, onDelete = ReferenceOption.CASCADE)
    val status = enumeration<PermissionStatus>("status")

    init {
        uniqueIndex(user, permission)
    }
}

class UserPermission(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserPermission>(UserPermissions) {
        fun hasPermission(user: User, permission: Permission): Boolean {
            val userPermission = find {
                (UserPermissions.user eq user.id) and
                        (UserPermissions.permission eq permission.id)
            }.firstOrNull()
            val userHasPermission = when (userPermission?.status) {
                PermissionStatus.Grant -> true
                PermissionStatus.Deny -> false
                null -> if (permission.parent != null) hasPermission(user, permission.parent!!)
                        else null
            }
            if (userHasPermission != null) return userHasPermission
            val groups = UserGroup.findGroupsSorted(user)
            for (group in groups) {
                val hasPermission = GroupPermission.hasPermission(group.group, permission)
                return hasPermission ?: continue
            }
            return false
        }
    }

    var user by User referencedOn UserPermissions.user
    var permission by Permission referencedOn UserPermissions.permission
    var status by UserPermissions.status
}