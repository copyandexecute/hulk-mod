package gg.norisk.hulk.mixin;

import gg.norisk.hulk.client.abilities.Punch;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(method = "attackBlock", at = @At("HEAD"))
    private void doAttackInjection(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player == null) return;
        Punch.INSTANCE.onAttack(blockPos);
    }
}
