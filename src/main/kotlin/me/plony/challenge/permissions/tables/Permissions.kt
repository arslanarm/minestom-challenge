package me.plony.challenge.permissions.tables

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.and

object Permissions : LongIdTable() {
    val name = text("name")
        .uniqueIndex()
    val parent = optReference("parent", Permissions, onDelete = ReferenceOption.CASCADE)
}

class Permission(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Permission>(Permissions) {
        fun findByName(name: String) = Permission.find { Permissions.name eq name }
        fun findByNameAndParent(name: String, parent: Permission?) = Permission.find { (Permissions.name eq name) and (Permissions.parent eq parent?.id) }
    }

    var name by Permissions.name
    var parent by Permission optionalReferencedOn Permissions.parent
    val children by Permission optionalReferrersOn Permissions.parent
    val users by User via UserPermissions
    val groups by Group via GroupPermissions
}

enum class PermissionStatus {
    Grant,
    Deny
}