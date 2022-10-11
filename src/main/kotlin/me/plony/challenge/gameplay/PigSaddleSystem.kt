package me.plony.challenge.gameplay

import net.minestom.server.entity.EquipmentSlot
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.animal.PigMeta
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.event.player.PlayerMoveEvent
import net.minestom.server.event.player.PlayerStartSneakingEvent
import net.minestom.server.event.player.PlayerTickEvent
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.listener.PlayerVehicleListener
import net.minestom.server.tag.Tag
import world.cepi.kstom.Manager
import world.cepi.kstom.event.listenOnly
import world.cepi.kstom.item.get

class PigSaddleSystem {
    fun init() {
        Manager.globalEvent.listenOnly<PlayerEntityInteractEvent> {
            val pig = target
            if (player.inventory.getItemInHand(hand).material() == Material.SADDLE && pig is Pig && !pig.getTag(Tag.Boolean("Saddle"))) {
                pig.equipSaddle()
            } else if (pig is Pig && pig.getTag(Tag.Boolean("Saddle")) && pig.passengers.isEmpty()) {
                pig.addPassenger(player)
            }
        }

        Manager.globalEvent.listenOnly<PlayerTickEvent> {
            if (player.vehicle != null && player.vehicleInformation.shouldUnmount()) {
                player.vehicle?.removePassenger(player)
                player.vehicleInformation.refresh(0f, 0f, false, false)
            }
        }
    }
}