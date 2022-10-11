package me.plony.challenge.permissions.tables

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Users : LongIdTable() {
    val username = text("username")
        .uniqueIndex()
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users) {
        fun findByName(user: String) = transaction {
            User.find { Users.username eq user }
        }
    }

    var username by Users.username
    val permissions by UserPermission referrersOn UserPermissions.user
    val groups by Group via UserGroups

    fun hasPermission(permission: Permission): Boolean =
        UserPermission.hasPermission(this, permission)
}