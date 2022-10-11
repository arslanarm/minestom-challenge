package me.plony.challenge.permissions.tables

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object UserGroups : LongIdTable() {
    val user = reference("user", Users)
    val group = reference("group", Groups)
    val priority = integer("priority")

    init {
        uniqueIndex(user, group)
    }
}

class UserGroup(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserGroup>(UserGroups) {
        fun findGroupsSorted(user: User) =
            find { UserGroups.user eq user.id }
                .sortedBy { it.priority }
    }

    var user by User referencedOn UserGroups.user
    var group by Group referencedOn UserGroups.group
    var priority by UserGroups.priority
}