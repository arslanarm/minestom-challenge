package me.plony.challenge.gameplay

import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.ai.EntityAIGroup
import net.minestom.server.entity.ai.EntityAIGroupBuilder
import net.minestom.server.entity.ai.GoalSelector
import net.minestom.server.entity.metadata.animal.PigMeta
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag

class Pig : EntityCreature(EntityType.PIG) {
    init {
        if (hasTag(Tag.Boolean("Saddle")) && getTag(Tag.Boolean("Saddle"))) {
            equipSaddle()
        }
        addAIGroup(EntityAIGroupBuilder()
            .addGoalSelector(PigCarrotStickGoal(this))
            .build())
    }

    fun equipSaddle() {
        setTag(Tag.Boolean("Saddle"), true)
        val pigMeta = entityMeta as PigMeta
        pigMeta.isHasSaddle = true
    }


}

class PigCarrotStickGoal(entityCreature: EntityCreature) : GoalSelector(entityCreature) {
    override fun shouldStart(): Boolean = entityCreature.passengers.any { it is Player && playerHasCarrotStick(it) }

    private fun playerHasCarrotStick(it: Player) =
        it.itemInMainHand.material() == Material.CARROT_ON_A_STICK

    override fun start() {
    }

    override fun tick(time: Long) {
        val passenger = entityCreature.passengers.first { it is Player && playerHasCarrotStick(it) } as Player
        entityCreature.navigator.moveTowards(passenger.position.direction(), 2.0)
    }

    override fun shouldEnd(): Boolean = entityCreature.passengers.none { it is Player && playerHasCarrotStick(it) }

    override fun end() {
    }

}