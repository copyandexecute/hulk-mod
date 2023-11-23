package gg.norisk.hulk.mixin;

import gg.norisk.hulk.common.entity.HulkPlayerKt;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyConstant(method = "computeFallDamage", constant = @Constant(floatValue = 3.0F))
    private float computeFallDamageInjection(float constant) {
        if ((LivingEntity) ((Object) this) instanceof PlayerEntity player && HulkPlayerKt.isHulk(player)) {
            return 9.0f;
        } else {
            return constant;
        }
    }

    @ModifyArgs(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void handleFallDamageInjection(Args args) {
        if ((LivingEntity) ((Object) this) instanceof PlayerEntity player && HulkPlayerKt.isHulk(player)) {
            args.set(1, (float) args.get(1) / 3f);
        }
    }
}
