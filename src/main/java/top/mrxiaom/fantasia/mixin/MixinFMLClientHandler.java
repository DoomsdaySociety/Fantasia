package top.mrxiaom.fantasia.mixin;

import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FMLClientHandler.class)
public class MixinFMLClientHandler {
    /* TODO 修改加载界面
    @Redirect(method = "beginMinecraftLoading", remap = false, at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/fml/client/SplashProgress;start()V"))
    public void SplashProgress$start()
    {

    }
    */
}
