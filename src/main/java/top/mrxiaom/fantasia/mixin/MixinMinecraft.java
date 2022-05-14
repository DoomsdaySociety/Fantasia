package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.Session;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mrxiaom.fantasia.ModWrapper;
import top.mrxiaom.fantasia.gui.FantasiaGuiMainMenu;

import javax.annotation.Nullable;

@Mixin(value = Minecraft.class, priority = 999)
public abstract class MixinMinecraft {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private boolean fullscreen;
    @Final
    @Shadow
    private Session session;

    @Shadow
    abstract void updateDisplayMode() throws LWJGLException;

    @Shadow @Nullable public GuiScreen currentScreen;

    @Shadow public abstract void displayGuiScreen(@Nullable GuiScreen guiScreenIn);

    /**
     * @author MrXiaoM
     * @reason 修改标题
     */
    @Inject(at = @At("RETURN"), method = "createDisplay")
    private void createDisplay(CallbackInfo ci) {
        ModWrapper.updateTitle();
    }

    /**
     * 覆盖 LiquidBounce 的主菜单替换
     */
    @Inject(method = "displayGuiScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", shift = At.Shift.AFTER))
    private void handleDisplayGuiScreen(CallbackInfo callbackInfo) {
        if (this.currentScreen instanceof GuiMainMenu) {
            this.currentScreen = new FantasiaGuiMainMenu();
            this.displayGuiScreen(this.currentScreen);
        }
    }
}
