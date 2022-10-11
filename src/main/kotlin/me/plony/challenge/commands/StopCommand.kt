package me.plony.challenge.commands

import me.plony.challenge.permissions.PermissionManager
import me.plony.challenge.permissions.permission
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.command.CommandSender
import net.minestom.server.command.ConsoleSender
import net.minestom.server.command.ServerSender
import net.minestom.server.entity.Player
import world.cepi.kstom.Manager
import world.cepi.kstom.command.kommand.Kommand
import kotlin.system.exitProcess

val CommandSender.name: String
    get() =
        when (this) {
            is Player -> username
            is ServerSender -> "Server"
            is ConsoleSender -> "Console"
            else -> "<Unknown $this>"
        }


class StopCommand(val manager: PermissionManager, val stopMessage: Component) : Kommand({
    permission(manager) {
        name = "server.stop"
        condition {
            checkPermission()
        }

        syntax {
            Manager.connection
                .onlinePlayers
                .forEach {
                    it.kick(stopMessage)
                }
            Manager.scheduler.scheduleNextTick {
                MinecraftServer.stopCleanly()
                exitProcess(0)
            }
        }

        subcommand("yes") {
            subPermission {
                name = "yes"
                syntax {

                }
            }
        }
    }
}, "stop")