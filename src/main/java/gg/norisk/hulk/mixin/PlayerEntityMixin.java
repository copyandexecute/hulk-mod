package gg.norisk.hulk.mixin;

import gg.norisk.hulk.client.abilities.HulkTransformation;
import gg.norisk.hulk.common.entity.HulkPlayerKt;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    protected HungerManager hungerManager;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTrackerInjecetion(CallbackInfo ci) {
        this.dataTracker.startTracking(HulkPlayerKt.getHulkTracker(), false);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (HulkPlayerKt.getHulkTracker().equals(data)) {
            if ((Object) this instanceof AbstractClientPlayerEntity clientPlayer) {
                HulkTransformation.INSTANCE.handleTransformation(clientPlayer);
            }
            this.calculateDimensions();
        }
    }

    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true)
    private void getActiveEyeHeightInjection(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if (HulkPlayerKt.isHulk((PlayerEntity) (Object) this)) {
            cir.setReturnValue(getDimensions(pose).height);
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true)
    private void getDimensionsInjection(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (HulkPlayerKt.isHulk((PlayerEntity) (Object) this)) {
            cir.setReturnValue(EntityDimensions.fixed(1.2f, 2.5f));
        }
    }

    @Override
    public float getJumpBoostVelocityModifier() {
        if (HulkPlayerKt.isHulk((PlayerEntity) (Object) this)) {
            return 0.5f;
        } else {
            return super.getJumpBoostVelocityModifier();
        }
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void regenerationInjection(CallbackInfo ci) {
        if (HulkPlayerKt.isHulk((PlayerEntity) (Object) this)) {
            if (this.getHealth() < this.getMaxHealth() && this.age % 3 == 0) {
                this.heal(1.0F);
            }

            if (this.hungerManager.isNotFull() && this.age % 5 == 0) {
                this.hungerManager.setFoodLevel(this.hungerManager.getFoodLevel() + 1);
            }
        }
    }


    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose);
    }
}
