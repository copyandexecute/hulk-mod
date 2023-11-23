package gg.norisk.hulk.mixin;

import gg.norisk.hulk.common.entity.HulkPlayerKt;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {
    @Shadow
    @Final
    public ModelPart leftArm;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    public BipedEntityModel.ArmPose leftArmPose;

    @Shadow
    public BipedEntityModel.ArmPose rightArmPose;

    @Override
    public void animateModel(T entity, float f, float g, float h) {
        super.animateModel(entity, f, g, h);
    }

    @Inject(method = "animateModel(Lnet/minecraft/entity/LivingEntity;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/AnimalModel;animateModel(Lnet/minecraft/entity/Entity;FFF)V"))
    private void animateModelInjection(T livingEntity, float f, float g, float h, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player && !livingEntity.getPassengerList().isEmpty() && HulkPlayerKt.isHulk(player)) {
            this.leftArmPose = this.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
        }
    }

    @Inject(method = "positionLeftArm", at = @At("HEAD"))
    private void positionLeftArmInjection(T livingEntity, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player && !livingEntity.getPassengerList().isEmpty() && HulkPlayerKt.isHulk(player)) {
            this.leftArm.pitch = this.leftArm.pitch * 0.5F - 1.2566371F;
            this.leftArm.yaw = 0.5235988F;
        }
    }

    @Inject(method = "positionRightArm", at = @At("HEAD"))
    private void positionRightArmInjection(T livingEntity, CallbackInfo ci) {
        if (livingEntity instanceof PlayerEntity player && !livingEntity.getPassengerList().isEmpty()  && HulkPlayerKt.isHulk(player)) {
            this.rightArm.pitch = this.rightArm.pitch * 0.5F - 1.2566371F;
            this.rightArm.yaw = -0.5235988F;
        }
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    private void setAnglesInjection(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        ModelPart var10000;
        if (livingEntity instanceof PlayerEntity player && !livingEntity.getPassengerList().isEmpty() && HulkPlayerKt.isHulk(player)) {
            --this.leftArm.pitch;
            --this.rightArm.pitch;
            var10000 = this.leftArm;
            var10000.pivotY -= 2.0F;
            var10000 = this.rightArm;
            var10000.pivotY -= 2.0F;
        }
    }
}
