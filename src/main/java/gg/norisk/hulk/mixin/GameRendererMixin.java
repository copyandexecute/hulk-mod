package gg.norisk.hulk.mixin;

import gg.norisk.hulk.common.entity.HulkPlayerKt;
import gg.norisk.hulk.common.entity.IHulkPlayer;
import gg.norisk.hulk.common.utils.CameraShaker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Credits to https://github.com/LoganDark/fabric-camera-shake
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final
    MinecraftClient client;

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void camerashake$onRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (!client.skipGameRender && tick && client.world != null) {
            CameraShaker.INSTANCE.newFrame();
        }
    }

    @Inject(
            method = "renderHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"
            )
    )
    private void camerashake$shakeHand(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        double x = CameraShaker.INSTANCE.getAvgX();
        double y = CameraShaker.INSTANCE.getAvgY();

        matrices.translate(x, -y, .0); // opposite of camera
    }

    @ModifyConstant(
            method = "updateTargetedEntity(F)V",
            require = 1, allow = 1, constant = @Constant(doubleValue = 6.0))
    private double getActualReachDistance(final double reachDistance) {
        if (this.client.player instanceof IHulkPlayer hulkPlayer && HulkPlayerKt.isHulk(client.player)) {
            return hulkPlayer.getGetCustomCreativeAttackReachDistance();
        }
        return reachDistance;
    }

    @ModifyConstant(method = "updateTargetedEntity(F)V", constant = @Constant(doubleValue = 3.0))
    private double getActualAttackRange0(final double attackRange) {
        if (this.client.player instanceof IHulkPlayer hulkPlayer && HulkPlayerKt.isHulk(client.player)) {
            return hulkPlayer.getGetCustomAttackReachDistance();
        }
        return attackRange;
    }

    @ModifyConstant(method = "updateTargetedEntity(F)V", constant = @Constant(doubleValue = 9.0))
    private double getActualAttackRange1(final double attackRange) {
        if (this.client.player instanceof IHulkPlayer hulkPlayer && HulkPlayerKt.isHulk(client.player)) {
            double reach;
            if (this.client.player.getAbilities().creativeMode) {
                reach = hulkPlayer.getGetCustomCreativeAttackReachDistance();
            } else {
                reach = hulkPlayer.getGetCustomAttackReachDistance();
            }
            return reach * reach;
        }
        return attackRange;
    }
}
