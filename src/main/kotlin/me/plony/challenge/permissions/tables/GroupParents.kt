package me.plony.challenge.permissions.tables

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object GroupParents : LongIdTable() {
    val parent = reference("parent", Groups)
    val child = reference("child", Groups)
    val priority = integer("priority")

    init {
        uniqueIndex(parent, child)
    }
}

class GroupParent(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupParent>(GroupParents) {
        fun findParentsSorted(group: Group) = find { GroupParents.child eq group.id }
            .sortedBy { it.priority }
            .map { it.parent }
    }

    var parent by Group referencedOn GroupParents.parent
    var child by Group referencedOn GroupParents.child
    var priority by GroupParents.priority
}