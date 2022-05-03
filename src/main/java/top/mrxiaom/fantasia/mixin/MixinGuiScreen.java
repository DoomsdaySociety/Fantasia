package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.mrxiaom.fantasia.ModWrapper;

@Mixin(GuiScreen.class)
public class MixinGuiScreen extends Gui {
    @Shadow public Minecraft mc;

    @Shadow public int width;

    @Shadow public int height;

    /**
     * @author MrXiaoM
     * @reason 修改背景
     */
    @Overwrite
    public void drawWorldBackground(int tint)
    {
        if (this.mc.world != null)
        {
            this.drawGradientRect(0, 0, this.width, this.height, 0x60000000, 0x80000000);
        }
        else
        {
            this.drawBackground(tint);
        }
    }
    /**
     * @author MrXiaoM
     * @reason 修改背景
     */
    @Overwrite
    public void drawBackground(int tint) {
        ModWrapper.drawBackground();
    }
}
