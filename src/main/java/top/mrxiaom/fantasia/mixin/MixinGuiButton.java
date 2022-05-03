package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.mrxiaom.fantasia.Utils;

@Mixin(GuiButton.class)
public abstract class MixinGuiButton extends Gui {
    long A = -1;
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    public int x;
    @Shadow
    public int y;
    @Shadow
    public String displayString;
    @Shadow
    public boolean enabled;
    @Shadow
    public boolean visible;
    @Shadow
    protected boolean hovered;

    @Shadow
    protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);
    /**
     * @author MrXiaoM
     * @reason 重写绘制按钮
     */
    @Overwrite
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean h = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            if (hovered != h) {
                if(enabled)
                    A = System.currentTimeMillis() - (A != 0 && System.currentTimeMillis() - A < 200L ? (System.currentTimeMillis() - A) : 0L);
                hovered = h;
            }
            float ani = MathHelper.clamp(hovered && enabled ? ((System.currentTimeMillis() - A) / 200.0F) : (1.0F - ((System.currentTimeMillis() - A) / 200.0F)), 0.0F, 1.0F);
            float ani1 = 2 * ani - ani * ani ;
            float aniW = 2.0F * ani1;
            float aniH = 2.0F * ani1;

            Utils.fillRect(x - aniW, y - aniH, x + width + aniW, y + height + aniH, 140 + 50 * ani1, 23, 23, 23);
            Utils.drawRect(x - aniW, y - aniH, x + width + aniW, y + height + aniH, 0.5f, 40 + 50 * ani1, 240,240,240);

            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;
            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }
            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }

}
