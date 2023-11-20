package gg.norisk.hulk.mixin;

import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import gg.norisk.hulk.client.abilities.HulkTransformation;
import gg.norisk.hulk.client.player.IAnimatedPlayer;
import gg.norisk.hulk.common.entity.HulkPlayerKt;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @ModifyArgs(method = "scale(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"))
    private void scaleInjection(Args args, AbstractClientPlayerEntity player, MatrixStack matrixStack, float f) {
        float hulkSize = HulkTransformation.INSTANCE.getCurrentSize();
        args.setAll(hulkSize, hulkSize, hulkSize);
        if (((IAnimatedPlayer) player).hulk_getModAnimation().getAnimation() instanceof KeyframeAnimationPlayer animation) {

        }
    }
}
