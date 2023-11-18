package gg.norisk.hulk.common.registry

import gg.norisk.hulk.common.ManagerCommon.toId
import gg.norisk.hulk.common.entity.HulkEntity
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.mob.PathAwareEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object EntityRegistry {
    val HULK =
        registerMob("hulk", ::HulkEntity, 1.5f, 1.5f)

    fun init() {
        FabricDefaultAttributeRegistry.register(HULK, createGenericEntityAttributes())
    }

    private fun <T : Entity> registerMob(
        name: String,
        entity: EntityType.EntityFactory<T>,
        width: Float,
        height: Float
    ): EntityType<T> {
        return Registry.register(
            Registries.ENTITY_TYPE,
            name.toId(),
            FabricEntityTypeBuilder
                .create(SpawnGroup.CREATURE, entity)
                .dimensions(EntityDimensions.changing(width, height))
                .build()
        )
    }

    private fun createGenericEntityAttributes(): DefaultAttributeContainer.Builder {
        return PathAwareEntity.createLivingAttributes()
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.80000000298023224)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0).add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
            .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.1)
    }
}
