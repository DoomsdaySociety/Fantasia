package top.mrxiaom.fantasia.mixin;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.mrxiaom.fantasia.ModWrapper;

import java.io.IOException;

@Mixin(SPacketChat.class)
public abstract class MixinSPacketChat implements Packet<INetHandlerPlayClient> {
    @Shadow
    private ITextComponent chatComponent;
    @Shadow
    private ChatType type;

    @Inject(method = "processPacket(Lnet/minecraft/network/play/INetHandlerPlayClient;)V", at = @At("HEAD"), cancellable = true)
    public void onChatReceived(INetHandlerPlayClient handler, CallbackInfo ci)
    {
        if (type.equals(ChatType.GAME_INFO))
            this.chatComponent = ModWrapper.onActionMessageReceived(chatComponent);
        else this.chatComponent = ModWrapper.onChatReceived(chatComponent, type);
        if (chatComponent == null)
            ci.cancel();
    }
}
