package me.plony.challenge.permissions

import world.cepi.kstom.adventure.asMini
import world.cepi.kstom.command.kommand.Kommand

class PermissionBuilder(val manager: PermissionManager, val parent: PermissionBuilder? = null) {
    var name: String? = null
    val children: MutableList<PermissionBuilder> = mutableListOf()

    inline fun Kommand.SyntaxContext.checkAndRun(block: () -> Unit) {
        if (manager.userHasPermission(player.username, name!!) == true) {
            block()
        } else {
            sender.sendMessage("<red>You don't have permission to run this command</red>".asMini())
        }
    }

    inline fun subPermission(block: PermissionBuilder.() -> Unit) {
        children += PermissionBuilder(manager, this).apply(block)
    }

    fun Kommand.ConditionContext.checkPermission(): Boolean {
        return (player?.username?.let { manager.userHasPermission(it, name!!) } == true).also {
            if (!it) sender.sendMessage("<red>You don't have permission to run this command</red>".asMini())
        }
    }

    fun build() {
        if (parent != null) {
            manager.register(name!!, parent.name!!)
        } else {
            val permissions = name!!.split('.')
            for (i in permissions.indices) {
                manager.register(
                    permissions.subList(0, i + 1).joinToString("."),
                    permissions.subList(0, i).joinToString(".")
                        .ifEmpty { null }
                )
            }
        }
        children.forEach { it.build() }
    }
}

inline fun permission(manager: PermissionManager, block: PermissionBuilder.() -> Unit) {
    PermissionBuilder(manager).apply(block)
        .build()
}