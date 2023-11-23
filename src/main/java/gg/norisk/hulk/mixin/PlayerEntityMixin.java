package gg.norisk.hulk.mixin;

import gg.norisk.hulk.client.abilities.HulkTransformation;
import gg.norisk.hulk.common.entity.HulkPlayerKt;
import gg.norisk.hulk.common.entity.IHulkPlayer;
import gg.norisk.hulk.common.registry.SoundRegistry;
import gg.norisk.hulk.common.utils.HulkUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IHulkPlayer {
    private static final TrackedData<Optional<BlockState>> PICKED_BLOCKSTATE = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);

    @Shadow
    protected HungerManager hungerManager;

    @Shadow
    protected abstract boolean clipAtLedge();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTrackerInjecetion(CallbackInfo ci) {
        this.dataTracker.startTracking(HulkPlayerKt.getHulkTracker(), false);
        this.dataTracker.startTracking(PICKED_BLOCKSTATE, Optional.empty());
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void attackInjection(Entity entity, CallbackInfo ci) {
        HulkUtils.INSTANCE.smashEntity((PlayerEntity) ((Object) this), entity);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (HulkPlayerKt.getHulkTracker().equals(data)) {
            if ((Object) this instanceof AbstractClientPlayerEntity clientPlayer) {
                HulkTransformation.INSTANCE.handleTransformation(clientPlayer);
                if (HulkPlayerKt.isHulk(clientPlayer)) {
                    setStepHeight(3.0f);
                } else {
                    setStepHeight(0.6f);
                }
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

    @Inject(method = "getHurtSound", at = @At("RETURN"), cancellable = true)
    private void getHurtSoundInjection(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> cir) {
        if (HulkPlayerKt.isHulk((PlayerEntity) (Object) this)) {
            cir.setReturnValue(SoundRegistry.INSTANCE.getRandomGrowlSound());
        }
    }

    @Inject(method = "getFallSounds", at = @At("RETURN"), cancellable = true)
    private void getFallSoundsInjection(CallbackInfoReturnable<FallSounds> cir) {
        if (HulkPlayerKt.isHulk((PlayerEntity) (Object) this)) {
            cir.setReturnValue(new FallSounds(SoundRegistry.INSTANCE.getRandomGrowlSound(), SoundRegistry.INSTANCE.getRandomGrowlSound()));
        }
    }

    @Override
    public void setGetCustomAttackReachDistance(double v) {

    }

    @Override
    public double getGetCustomAttackReachDistance() {
        return 6.0;
    }

    @Override
    public void setGetCustomCreativeAttackReachDistance(double v) {

    }

    @Override
    public double getGetCustomCreativeAttackReachDistance() {
        return 6.0;
    }

    @Override
    public void setGetCustomBlockReachDistance(float v) {

    }

    @Override
    public float getGetCustomBlockReachDistance() {
        return 8.0f;
    }

    @Override
    public void setGetCustomCreativeBlockReachDistance(float v) {

    }

    @Override
    public double getMountedHeightOffset() {
        if (HulkPlayerKt.isHulk((PlayerEntity) (Object) this)) {
            return (double) this.getDimensions(this.getPose()).height;
        } else {
            return super.getMountedHeightOffset();
        }
    }

    @Nullable
    @Override
    public BlockState getBlockStateInHand() {
        return (BlockState) ((Optional) this.dataTracker.get(PICKED_BLOCKSTATE)).orElse(null);
    }

    @Override
    public void setBlockStateInHand(@Nullable BlockState blockState) {
        this.dataTracker.set(PICKED_BLOCKSTATE, Optional.ofNullable(blockState));
    }

    @Override
    public float getGetCustomCreativeBlockReachDistance() {
        return 8.0f;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose);
    }
}
