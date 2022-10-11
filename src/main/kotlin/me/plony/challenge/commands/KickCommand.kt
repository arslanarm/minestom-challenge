package me.plony.challenge.commands

import me.plony.challenge.permissions.PermissionManager
import me.plony.challenge.permissions.permission
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import world.cepi.kstom.adventure.plainText
import world.cepi.kstom.command.arguments.ArgumentPlayer
import world.cepi.kstom.command.arguments.MiniMessageArgument
import world.cepi.kstom.command.kommand.Kommand

class KickCommand(val manager: PermissionManager, val defaultMessage: Component) : Kommand({
    val playerArgument = ArgumentPlayer("player")
    val messageArgument = MiniMessageArgument.single("message")


    permission(manager) {
        name = "server.kick"

        condition {
            checkPermission()
        }


        syntax {
            sender.sendMessage("Usage: /kick <player> [message]")
        }

        syntax(playerArgument) {
            val target = !playerArgument
            target.kick(defaultMessage)
        }

        syntax(playerArgument, messageArgument) {
            val target = !playerArgument
            val message = !messageArgument
            target.kick(message)
        }
    }
}, "kick")