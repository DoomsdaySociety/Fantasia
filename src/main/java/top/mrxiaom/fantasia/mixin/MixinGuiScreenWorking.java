package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.mrxiaom.fantasia.ModWrapper;

@Mixin(GuiScreenWorking.class)
public class MixinGuiScreenWorking extends GuiScreen{

    @Shadow private boolean doneWorking;

    @Shadow private String title;

    @Shadow private String stage;

    @Shadow private int progress;

    /**
     * @author MrXiaoM
     * @reason 修改背景
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.doneWorking)
        {
            if (!this.mc.isConnectedToRealms())
            {
                this.mc.displayGuiScreen((GuiScreen)null);
            }
        }
        else
        {
            ModWrapper.drawBackground();
            this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
            this.drawCenteredString(this.fontRenderer, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}
