package me.plony.challenge.permissions

import me.plony.challenge.permissions.tables.*
import net.minestom.server.event.player.PlayerLoginEvent
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly

fun configureDatabase() {
    Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    transaction {
        SchemaUtils.create(
            Groups,
            Users,
            GroupParents,
            Permissions,
            GroupPermissions,
            UserGroups,
            UserPermissions
        )
    }
}

class PermissionManager {
    init {
        configureDatabase()
        createGroup("default")

        Manager.globalEvent.listenOnly<PlayerLoginEvent> {
            createUser(player.username)

            if (getUserGroups(player.username)?.isNotEmpty() != true)
                addUserToGroup(player.username, "default")

        }
    }

    fun register(permission: String, parent: String? = null): Long {
        val parentName = parent ?:
            if ('.' in permission) permission.dropLastWhile { it != '.' }.dropLast(0)
            else null
        return transaction {
            val parentPermission = parentName?.let { Permission.findByName(it).firstOrNull() }
            Permission.findByNameAndParent(permission, parentPermission)
                .firstOrNull()?.id?.value
                ?: Permission.new {
                    this.name = permission
                    this.parent = parentPermission
                }.id.value
        }
    }

    fun delete(permission: String) {
        transaction {
            Permission.findByName(permission)
                .firstOrNull()
                ?.delete()
        }
    }

    fun addPermissionToUser(username: String, permissionName: String, status: Boolean) {
        transaction {
            val user = User.findByName(username).first()
            val permission = Permission.findByName(permissionName).first()

            UserPermission.new {
                this.user = user
                this.permission = permission
                this.status = if (status) PermissionStatus.Grant else PermissionStatus.Deny
            }
        }
    }

    fun createUser(username: String) {
        transaction {
            User.findByName(username).firstOrNull()
                ?: User.new {
                    this.username = username
                }
        }
    }

    fun getUsers() = transaction {
        User.all().map { it.username }
    }

    fun getUserGroups(username: String) = transaction {
        User.findByName(username).firstOrNull()
            ?.groups
            ?.map { it.name }
    }

    fun getUserPermissions(username: String) = transaction {
        User.findByName(username).firstOrNull()
            ?.permissions
            ?.map { it.permission.name }
    }

    fun userHasPermission(username: String, permissionName: String) = transaction {
        Permission.findByName(permissionName).firstOrNull()?.let {
            User.findByName(username).firstOrNull()
                ?.hasPermission(it)
        }
    }

    fun createGroup(groupName: String) {
        transaction {
            Group.findByName(groupName).firstOrNull()
                ?: Group.new {
                    name = groupName
                }
        }
    }

    fun getGroup() = transaction {
        Group.all().map { it.name }
    }

    fun getGroupPermissions(groupName: String) = transaction {
        Group.findByName(groupName).firstOrNull()
            ?.permissions
            ?.map { it.name }
    }
    fun groupHasPermission(groupName: String, permissionName: String) = transaction {
        Permission.findByName(permissionName).firstOrNull()?.let {
            Group.findByName(groupName).firstOrNull()
                ?.hasPermission(it)
        }
    }

    fun addParentGroup(child: String, parent: String, priority: Int = 0) {
        transaction {
            GroupParent.new {
                this.parent = Group.findByName(parent).first()
                this.child = Group.findByName(child).first()
                this.priority = priority
            }
        }
    }
    fun removeParentGroup(child: String, parent: String) {
        transaction {
            GroupParent.find {
                (GroupParents.parent eq  Group.findByName(parent).first().id) and
                        (GroupParents.child eq  Group.findByName(child).first().id)
            }.forEach { it.delete() }
        }
    }

    fun addUserToGroup(username: String, groupName: String, priority: Int = 0) {
        transaction {
            UserGroup.new {
                user = User.findByName(username).first()
                group = Group.findByName(groupName).first()
                this.priority = priority
            }
        }
    }

    fun removeUserFromGroup(username: String, groupName: String) {
        transaction {
            UserGroup.find {
                (UserGroups.user eq User.findByName(username).first().id) and
                        (UserGroups.group eq Group.findByName(groupName).first().id)
            }.forEach { it.delete() }
        }
    }

    fun addPermissionToGroup(groupName: String, permissionName: String, status: Boolean) {
         transaction {
            val group = Group.findByName(groupName).first()
            val permission = Permission.findByName(permissionName).first()

            GroupPermission.new {
                this.group = group
                this.permission = permission
                this.status = if (status) PermissionStatus.Grant else PermissionStatus.Deny
            }
        }
    }

    fun removePermissionFromUser(username: String, permissionName: String) {
       transaction {
           val user = User.findByName(username).first()
           val permission = Permission.findByName(permissionName).first()
           UserPermission.find { (UserPermissions.user eq user.id) and (UserPermissions.permission eq permission.id) }
               .forEach { it.delete() }
       }
    }

    fun removePermissionFromGroup(groupname: String, permissionName: String) {
       transaction {
           val user = Group.findByName(groupname).first()
           val permission = Permission.findByName(permissionName).first()
           GroupPermission.find { (GroupPermissions.group eq user.id) and (GroupPermissions.permission eq permission.id) }
               .forEach { it.delete() }
       }
    }

    fun getGroupParents(groupName: String) = transaction {
        GroupParent.findParentsSorted(Group.findByName(groupName).first())
            .map { it.name }
    }
}