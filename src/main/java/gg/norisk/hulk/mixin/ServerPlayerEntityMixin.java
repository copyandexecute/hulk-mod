package gg.norisk.hulk.mixin;

import com.mojang.authlib.GameProfile;
import gg.norisk.hulk.common.network.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(world, blockPos, f, gameProfile);
    }

    @Inject(method = "swingHand", at = @At("HEAD"))
    private void swingHandInjection(Hand hand, CallbackInfo ci) {
        NetworkManager.INSTANCE.throwPassengers((ServerPlayerEntity) ((Object) this));
    }
}
