package top.mrxiaom.fantasia;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class GuiConfig extends GuiScreen {
    GuiScreen last;
    public GuiConfig(GuiScreen last) {
        this.last = last;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, width / 2 + 2, height - 32, 98, 20, I18n.format("gui.back")));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height - 32, 98, 20, "保存"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(last);
        }
        if (button.id == 1) {

            this.mc.displayGuiScreen(last);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWorldBackground(0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
