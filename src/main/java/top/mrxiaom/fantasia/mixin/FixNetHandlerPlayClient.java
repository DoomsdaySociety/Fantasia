package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(NetHandlerPlayClient.class)
public abstract class FixNetHandlerPlayClient {

    @Shadow public abstract NetworkPlayerInfo getPlayerInfo(UUID uniqueId);
    @Inject(at = @At("HEAD"), method = "handleSpawnPlayer*", cancellable = true)
    public void handleSpawnPlayer(SPacketSpawnPlayer packetIn, CallbackInfo ci) {
        if (packetIn == null || getPlayerInfo(packetIn.getUniqueId()) == null) ci.cancel();
    }
}
