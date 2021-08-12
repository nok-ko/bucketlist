package me.nokko.bucketlist.mixin;

import me.nokko.bucketlist.BucketListClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// It'll be easy, they said…
// This jank is because the 1.16.1 networking API (or possibly just 1.16.1) doesn't fire the disconnect
// event when you leave a singleplayer/integrated server world.
// And I am not gonna figure out the actual right event to call, so unnecessary Mixin.
// (And horrible hardcoded event)
@Environment(EnvType.CLIENT)
@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Inject(method = "disconnect(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    public void onDisconnect(Text reason, CallbackInfo ci) {
        // 3 threads can call into this, so check that we're on the one we want
        // theoretically we won't overwrite anything if we save multiple times but
        // it's better to be safe than trust my janky code.
        if (MinecraftClient.getInstance().isOnThread()) {
            BucketListClient.onPlayDisconnect(MinecraftClient.getInstance().getNetworkHandler(), MinecraftClient.getInstance());
        }
    }
}
