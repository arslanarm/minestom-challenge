package me.plony.challenge.commands

import me.plony.challenge.permissions.PermissionManager
import me.plony.challenge.permissions.permission
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.arguments.minecraft.ArgumentItemStack
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import world.cepi.kstom.command.arguments.ArgumentPlayer
import world.cepi.kstom.command.arguments.defaultValue
import world.cepi.kstom.command.kommand.Kommand


class GiveCommand(val manager: PermissionManager) : Kommand({
    val playerArgument = ArgumentPlayer("player")
    val itemArgument = ArgumentItemStack("item")


    permission(manager) {
        name = "server.give"
        condition {
            checkPermission()
        }

        syntax {
            sender.sendMessage("Usage: /give <item> [player]")
        }

        syntax(itemArgument) {
            val player = sender as Player
            val item = !itemArgument
            player.give(item)
            sender.sendMessage(
                "Successfully gave ${
                    item.displayName ?: item.material().name()
                }x${item.amount()} to ${player.username}"
            )
        }.onlyPlayers()

        syntax(itemArgument, playerArgument) {
            val player = !playerArgument
            val item = !itemArgument
            player.give(item)
            sender.sendMessage(
                "Successfully gave ${
                    item.displayName ?: item.material().name()
                }x${item.amount()} to ${player.username}"
            )
        }
    }
}, "give")

fun Player.give(item: ItemStack) {
    if (!inventory.addItemStack(item)) {
        dropItem(item)
    }
}


