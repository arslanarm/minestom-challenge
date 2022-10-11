package me.plony.challenge.commands

import me.plony.challenge.permissions.PermissionManager
import me.plony.challenge.permissions.permission
import net.minestom.server.command.builder.arguments.ArgumentEnum
import net.minestom.server.command.builder.arguments.ArgumentLiteral
import net.minestom.server.command.builder.arguments.ArgumentString
import net.minestom.server.entity.GameMode.*
import world.cepi.kstom.command.kommand.Kommand

enum class GameMode {
    Creative, C, // Creative
    Survival, S, // Survival
    Adventure, A, // Adventure,
    Spectator
}

class GameModeCommand(manager: PermissionManager) : Kommand({
    val mode = ArgumentEnum("mode", GameMode::class.java)
        .setFormat(ArgumentEnum.Format.LOWER_CASED)

    permission(manager) {
        name = "server.gamemode"
        syntax(mode) {
            player.gameMode = when (!mode) {
                GameMode.Creative, GameMode.C -> CREATIVE
                GameMode.Survival, GameMode.S -> SURVIVAL
                GameMode.Adventure, GameMode.A -> ADVENTURE
                GameMode.Spectator -> SPECTATOR
                null -> error("Cannot happen")
            }
        }.onlyPlayers()
    }
}, "gamemode", "gm")