package me.plony.challenge.permissions.tables

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select

object GroupPermissions : LongIdTable() {
    val group = reference("group", Groups)
    val permission = reference("permission", Permissions)
    val status = enumeration<PermissionStatus>("status")
    init {
        uniqueIndex(group, permission)
    }
}

class GroupPermission(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupPermission>(GroupPermissions) {
        fun hasPermission(group: Group, permission: Permission): Boolean? {
            val groupPermission = find {
                (GroupPermissions.group eq group.id) and
                        (GroupPermissions.permission eq permission.id)
            }.firstOrNull()
            return when (groupPermission?.status) {
                PermissionStatus.Grant -> true
                PermissionStatus.Deny -> false
                null -> {
                    if (permission.parent != null) {
                        hasPermission(group, permission.parent!!)
                    }
                    if (group.parents.count() != 0L) {
                        val parents = GroupParent.findParentsSorted(group)
                        var currentValue: Boolean? = null
                        for (parent in parents) {
                            currentValue = hasPermission(parent, permission)
                            if (currentValue != null) break
                        }
                        currentValue
                    } else null
                }
            }
        }
    }

    var group by Group referencedOn GroupPermissions.group
    var permission by Permission referencedOn GroupPermissions.permission
    var status by GroupPermissions.status
}