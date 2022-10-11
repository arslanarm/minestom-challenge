package me.plony.challenge

import me.plony.challenge.commands.*
import me.plony.challenge.gameplay.PigEggSpawnerSystem
import me.plony.challenge.gameplay.PigSaddleSystem
import me.plony.challenge.permissions.PermissionManager
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.instance.block.Block
import world.cepi.kstom.Manager
import world.cepi.kstom.adventure.asComponent
import world.cepi.kstom.adventure.asMini
import world.cepi.kstom.command.kommand.Kommand
import world.cepi.kstom.event.listen
import world.cepi.kstom.event.listenOnly

fun main() {
    val server = MinecraftServer.init()
    val instance = Manager.instance
        .createInstanceContainer()

    instance.setGenerator { it.modifier().fillHeight(0, 40, Block.GRASS_BLOCK) }

    Manager.globalEvent
        .listenOnly<PlayerLoginEvent> {
            setSpawningInstance(instance)
            player.respawnPoint = Pos(0.0, 40.0, 0.0)
        }
    val permissionManager = PermissionManager()
    KickCommand(permissionManager, "Oh nyo, you are <rainbow>kicked</rainbow>".asMini()).register()
    StopCommand(permissionManager, "Server stopped".asMini()).register()
    GiveCommand(permissionManager,).register()
    GameModeCommand(permissionManager,).register()

    PermissionCommand(permissionManager).register()

    PigEggSpawnerSystem().init()
    PigSaddleSystem().init()
    server.start("0.0.0.0", 25565)
}