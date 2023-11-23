package gg.norisk.hulk.mixin;

import gg.norisk.hulk.client.abilities.Punch;
import gg.norisk.hulk.common.entity.HulkPlayerKt;
import gg.norisk.hulk.common.entity.IHulkPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private GameMode gameMode;

    @Inject(method = "attackBlock", at = @At("HEAD"))
    private void doAttackInjection(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;
        Punch.INSTANCE.onAttack(blockPos);
    }

    //Für Blöcke -> Blöcke anvisieren hat höhere / andere Range
    @Inject(at = {@At("HEAD")}, method = {"getReachDistance()F"}, cancellable = true)
    private void onGetReachDistance(CallbackInfoReturnable<Float> cir) {
        if (this.client.player instanceof IHulkPlayer hulkPlayer && HulkPlayerKt.isHulk(client.player)) {
            cir.setReturnValue(this.gameMode.isCreative() ? hulkPlayer.getGetCustomCreativeBlockReachDistance() : hulkPlayer.getGetCustomBlockReachDistance());
        }
    }
}
