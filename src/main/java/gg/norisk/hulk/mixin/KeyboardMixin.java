package gg.norisk.hulk.mixin;

import gg.norisk.hulk.client.event.BasicEventsKt;
import gg.norisk.hulk.client.event.KeyEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    /**
     * Hook key event
     */
    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;fromKeyCode(II)Lnet/minecraft/client/util/InputUtil$Key;", shift = At.Shift.AFTER))
    private void onKeyInjection(long window, int key, int scancode, int action, int j, CallbackInfo callback) {
        BasicEventsKt.getKeyEvent().invoke(new KeyEvent(key, scancode, action, client));
    }
}
