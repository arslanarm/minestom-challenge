package me.plony.challenge.permissions.tables

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Groups : LongIdTable() {
    val name = text("name")
        .uniqueIndex()

}

class Group(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Group>(Groups) {
        fun findByName(name: String) = find { Groups.name eq name }
    }

    var name by Groups.name
    val parents by Group.via(GroupParents.child, GroupParents.parent)
    val children by Group.via(GroupParents.parent, GroupParents.child)

    val permissions by Permission via GroupPermissions
    val users by User via UserGroups

    fun hasPermission(permission: Permission) = GroupPermission.hasPermission(this, permission)
}