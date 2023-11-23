package gg.norisk.hulk.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "removePassenger", at = @At("TAIL"))
    private void onRemovePassenger(Entity passenger, CallbackInfo callbackInfo) {
        Entity entity = (Entity) (Object) this;
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity) {
            ((ServerPlayerEntity) entity).networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;Z)Z", at = @At("TAIL"))
    private void onStartRiding(Entity entity, boolean force, CallbackInfoReturnable<Boolean> cir) {
        if (!entity.getWorld().isClient && entity instanceof PlayerEntity) {
            ((ServerPlayerEntity) entity).networkHandler.sendPacket(new EntityPassengersSetS2CPacket(entity));
        }
    }
}