package me.plony.challenge.gameplay

import net.minestom.server.event.player.PlayerUseItemOnBlockEvent
import net.minestom.server.item.Material
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly

class PigEggSpawnerSystem {
    fun init() {
        Manager.globalEvent.listenOnly<PlayerUseItemOnBlockEvent> {
            if (itemStack.material() == Material.PIG_SPAWN_EGG) {
                val direction = blockFace.toDirection()
                val pig = Pig()
                pig.setInstance(player.instance!!, this.position.add(direction.normalX()*1.0, direction.normalY()*1.0, direction.normalZ()*1.0))
            }
        }
    }
}