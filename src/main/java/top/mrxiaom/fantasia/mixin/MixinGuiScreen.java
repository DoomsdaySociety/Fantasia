package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mrxiaom.fantasia.FMLPlugin;
import top.mrxiaom.fantasia.ModWrapper;

@Mixin(value = GuiScreen.class, priority = 999)
public abstract class MixinGuiScreen extends Gui {
    @Shadow public abstract void drawWorldBackground(int tint);

    /**
     * @author MrXiaoM
     * @reason 修改背景
     */
    @Overwrite
    public void drawDefaultBackground() {
        this.drawWorldBackground(0);
    }
    @Inject(at = @At("HEAD"), method = "drawBackground", cancellable = true)
    public void drawBackground(int tint, CallbackInfo ci) {
        if (FMLPlugin.getMainMenuConfig().overrideBackground) {
            ModWrapper.drawBackground();
            ci.cancel();
        }
    }
}
