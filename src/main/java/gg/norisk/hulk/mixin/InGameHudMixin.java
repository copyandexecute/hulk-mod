package gg.norisk.hulk.mixin;

import gg.norisk.hulk.client.abilities.HulkTransformation;
import gg.norisk.hulk.common.ManagerCommon;
import gg.norisk.hulk.common.entity.HulkPlayerKt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique
    private static final Identifier FULL = ManagerCommon.INSTANCE.toId("textures/green_heart_full.png");
    @Unique
    private static final Identifier HALF = ManagerCommon.INSTANCE.toId("textures/green_heart_half.png");

    @Inject(method = "drawHeart", at = @At("HEAD"), cancellable = true)
    private void drawHeartInjection(DrawContext drawContext, InGameHud.HeartType heartType, int i, int j, int k, boolean bl, boolean bl2, CallbackInfo ci) {
        var player = MinecraftClient.getInstance().player;
        if (heartType == InGameHud.HeartType.CONTAINER) return;
        if (player == null) return;
        if (HulkPlayerKt.isHulk(player)) {
            if (!HulkTransformation.INSTANCE.getUseHulkSkin()) return;
            if (bl2) {
                drawContext.drawTexture(HALF, i, j, 9, 9, 9, 9, 9, 9);
            } else {
                drawContext.drawTexture(FULL, i, j, 9, 9, 9, 9, 9, 9);
            }
            ci.cancel();
        }
    }
}
