package gg.norisk.hulk.mixin;

import gg.norisk.hulk.common.entity.HulkPlayerKt;
import gg.norisk.hulk.common.network.NetworkManager;
import gg.norisk.hulk.common.utils.HulkUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Iterator;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    public LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "computeFallDamage", constant = @Constant(floatValue = 3.0F))
    private float computeFallDamageInjection(float constant) {
        if ((LivingEntity) ((Object) this) instanceof PlayerEntity player && HulkPlayerKt.isHulk(player)) {
            return 9.0f;
        } else {
            return constant;
        }
    }

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void tickMovementInjection(CallbackInfo ci) {
        boolean isThrown = NetworkManager.INSTANCE.getFlyingEntities().contains(this.getUuid());

        if (this.horizontalCollision && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && isThrown) {
            for (BlockPos blockPos : HulkUtils.INSTANCE.generateSphere(this.getBlockPos(), 3, false)) {
                BlockState blockState = getWorld().getBlockState(blockPos);
                if (blockState.isAir()) continue;
                getWorld().breakBlock(blockPos,false);
            }
        }
        if (this.isOnGround() && isThrown) {
            NetworkManager.INSTANCE.getFlyingEntities().remove(this.getUuid());
        }
    }

    @ModifyArgs(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void handleFallDamageInjection(Args args) {
        if ((LivingEntity) ((Object) this) instanceof PlayerEntity player && HulkPlayerKt.isHulk(player)) {
            args.set(1, (float) args.get(1) / 3f);
        }
    }
}
