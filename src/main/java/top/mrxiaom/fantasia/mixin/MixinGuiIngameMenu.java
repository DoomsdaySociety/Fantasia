package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mrxiaom.fantasia.FMLPlugin;
import top.mrxiaom.fantasia.ModWrapper;

import java.io.IOException;

@Mixin(GuiIngameMenu.class)
public class MixinGuiIngameMenu extends GuiScreen {

    @Inject(at = @At("RETURN"), method = "initGui")
    public void initGui(CallbackInfo ci) {
        this.buttonList.add(new GuiButton(7, 5, 5, 100, 20, "重载 Fantasia"));
    }

    @Inject(at = @At("RETURN"), method = "actionPerformed")
    protected void actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 7) {
            FMLPlugin.reloadConfig();
            Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages("配置文件已重载");
        }
    }
}
