package top.mrxiaom.fantasia.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import top.mrxiaom.fantasia.ModWrapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GuiConfig extends GuiScreen {
    GuiScreen last;
    private GuiTextField nameEdit;

    public GuiConfig(GuiScreen last) {
        this.last = last;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.add(new GuiButton(0, width / 2 + 2, height - 32, 98, 20, I18n.format("gui.back")));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height - 32, 98, 20, "保存"));
        this.nameEdit = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
        this.nameEdit.setMaxStringLength(128);
        this.nameEdit.setFocused(true);
        this.nameEdit.setText(this.mc.getSession().getUsername());

        this.buttonList.add(new GuiButton(7, 5, 5, 100, 20, "重载 Fantasia"));
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(last);
        }
        if (button.id == 1) {
            try {
                String oldName = this.mc.getSession().getUsername();
                String newName = this.nameEdit.getText();
                Class<?> minecraft = mc.getClass();
                boolean ok = false;
                for (Field f : minecraft.getDeclaredFields()) {
                    if (ok) break;
                    if (!f.isAccessible()) f.setAccessible(true);
                    if (f.getGenericType().getTypeName().equals(Session.class.getTypeName())) {
                        Object obj = f.get(mc);
                        Class<?> cls = Session.class;
                        for (Field f2 : cls.getDeclaredFields()) {
                            if (!f2.isAccessible()) f2.setAccessible(true);
                            if (oldName.equals(f2.get(obj))) {
                                Field modifiers = Field.class.getDeclaredField("modifiers");
                                modifiers.setAccessible(true);
                                modifiers.setInt(f2, f2.getModifiers() & ~Modifier.FINAL);
                                f2.set(obj, newName);
                                ok = true;
                                break;
                            }
                        }
                    }
                }
                if (!ok) throw new IllegalStateException("Unable to hack Minecraft user name!");
                ModWrapper.updateTitle();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            this.mc.displayGuiScreen(last);
        }
        if (button.id == 7) {
            ModWrapper.reloadConfig();
            this.mc.displayGuiScreen(last);
        }
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) throws IOException {
        if (!nameEdit.textboxKeyTyped(p_73869_1_, p_73869_2_))
            super.keyTyped(p_73869_1_, p_73869_2_);
    }

    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException {
        if (!nameEdit.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_))
            super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWorldBackground(0);
        nameEdit.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
