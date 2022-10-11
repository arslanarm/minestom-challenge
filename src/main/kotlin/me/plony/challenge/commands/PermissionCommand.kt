package me.plony.challenge.commands

import kotlinx.coroutines.Dispatchers
import me.plony.challenge.permissions.PermissionManager
import net.minestom.server.command.builder.arguments.ArgumentBoolean
import net.minestom.server.command.builder.arguments.ArgumentString
import net.minestom.server.command.builder.arguments.number.ArgumentInteger
import world.cepi.kstom.command.arguments.defaultValue
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.kommand.Kommand

class PermissionCommand(manager: PermissionManager) : Kommand({
    val user by literal
    val group by literal
    val permission by literal
    val parent by literal
    val name = ArgumentString("name")
    val groupName = ArgumentString("groupName")
    val add by literal
    val remove by literal
    val create by literal
    val permissionName = ArgumentString("permissionName")
    val status = ArgumentBoolean("status").defaultValue(true)
    val priority = ArgumentInteger("priority").defaultValue(0)

    syntaxSuspending {
        val users = manager.getUsers()
        val groups = manager.getGroup()

        val message = "Users:" + users.joinWithLS() +
                "\n------\nGroups:" + groups.joinWithLS()

        sender.sendMessage(message)
    }
    syntaxSuspending(Dispatchers.IO, user, name) {
        val groups = manager.getUserGroups(!name)
        val permissions = manager.getUserPermissions(!name)

        val message = if (groups != null && permissions != null)
            """
                |User: ${!name}
                |Groups: ${groups.joinToString()}
                |Permissions:
            """.trimMargin() + permissions.joinWithLS()
        else "User ${!name} doesn't exist"

        sender.sendMessage(message)
    }

    syntaxSuspending(Dispatchers.IO, group, name) {
        val permissions = manager.getGroupPermissions(!name)

        val message = if (permissions != null)
            """
                |Group: ${!name}
                |Permissions:
                |
            """.trimMargin() + permissions.joinWithLS()
        else "Group ${!name} doesn't exist"

        sender.sendMessage(message)
    }

    syntaxSuspending(Dispatchers.IO, user, name, permission, add, permissionName, status) {
        manager.addPermissionToUser(!name, !permissionName, !status)
        sender.sendMessage("Successfully added permission to user")
    }
    syntaxSuspending(Dispatchers.IO, user, name, permission, remove, permissionName) {
        manager.removePermissionFromUser(!name, !permissionName)
        sender.sendMessage("Successfully removed permission from user")
    }

    syntaxSuspending(Dispatchers.IO, group, name, permission, add, permissionName, status) {
        manager.addPermissionToGroup(!name, !permissionName, !status)
        sender.sendMessage("Successfully added permission to group")
    }
    syntaxSuspending(Dispatchers.IO, group, name, permission, remove, permissionName) {
        manager.removePermissionFromGroup(!name, !permissionName)
        sender.sendMessage("Successfully removed permission from group")
    }

    syntaxSuspending(Dispatchers.IO, user, name, group, add, groupName, priority) {
        manager.addUserToGroup(!name, !groupName, !priority)
        sender.sendMessage("Successfully added ${!name} to ${!groupName}")
    }
    syntaxSuspending(Dispatchers.IO, user, name, group, remove, groupName) {
        manager.removeUserFromGroup(!name, !groupName)
        sender.sendMessage("Successfully removed ${!name} from ${!groupName}")
    }

    syntaxSuspending(Dispatchers.IO, create, group, groupName) {
        manager.createGroup(!groupName)
        sender.sendMessage("Successfully created group: ${!groupName}")
    }

    syntaxSuspending(Dispatchers.IO, group, name, parent) {
        val parents = manager.getGroupParents(!name)
        sender.sendMessage("""
            |Group: ${!name}
            |Parents:
        """.trimMargin() + parents.joinWithLS()
        )
    }

    syntaxSuspending(Dispatchers.IO, group, name, parent, add, groupName, priority) {
        manager.addParentGroup(!name, !groupName, !priority)
        sender.sendMessage("Successfully added ${!groupName} as ${!name}'s parent")
    }
    syntaxSuspending(Dispatchers.IO, group, name, parent, remove, groupName) {
        manager.removeParentGroup(!name, !groupName)
        sender.sendMessage("Successfully removed ${!groupName} from ${!name}")
    }
}, "perm")

private fun List<String>.joinWithLS() = joinToString("") { "\n$it" }