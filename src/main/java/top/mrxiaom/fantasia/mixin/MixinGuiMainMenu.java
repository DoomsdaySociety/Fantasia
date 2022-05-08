package top.mrxiaom.fantasia.mixin;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mrxiaom.fantasia.ModWrapper;
import top.mrxiaom.fantasia.gui.GuiConfig;
import top.mrxiaom.fantasia.gui.GuiPasswordField;
import top.mrxiaom.fantasia.gui.GuiServerList;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {
    private static final ResourceLocation LANGUAGE_ICON = new ResourceLocation("fantasia", "textures/gui/languages.png");

    @Shadow
    private String splashText;
    @Shadow
    @Final
    private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
    @Shadow
    @Final
    private static final ResourceLocation field_194400_H = new ResourceLocation("textures/gui/title/edition.png");
    @Shadow
    private int widthCopyright;
    @Shadow
    private int widthCopyrightRest;
    private net.minecraftforge.client.gui.NotificationModUpdateScreen modUpdateNotification;
    @Shadow
    private float panoramaTimer;
    private GuiButton modButton;
    private GuiButton langButton;
    private static final String copyrightString = "Mojang AB 版权所有， 禁止盗版! ";
    GuiServerList serverList;
    boolean init = false;
    List<ServerData> servers;
    GuiPasswordField pwField;

    /**
     * @author MrXiaoM
     * @reason 重写初始化
     */
    @Overwrite
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.widthCopyright = this.fontRenderer.getStringWidth(copyrightString);
        this.widthCopyrightRest = this.width - this.widthCopyright - 2;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) == 24) {
            this.splashText = "Merry X-mas!";
        } else if (calendar.get(Calendar.MONTH) + 1 == 1 && calendar.get(Calendar.DATE) == 1) {
            this.splashText = "Happy new year!";
        } else if (calendar.get(Calendar.MONTH) + 1 == 10 && calendar.get(Calendar.DATE) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }
        int i = this.width / 2 - 126;
        int j = this.height / 2 - 30;
        int serverX = this.width / 2 + 2;
        int serverY = j + 10;
        int k = serverY + 65;
        if (init) {
            serverList.setDimensions(100, servers.size() * 36 + 4, serverX, serverY);
            pwField.setDimensions(i, j);
        } else {
            init = true;
            FMLClientHandler.instance().setupServerList();
            servers = ModWrapper.getMainMenuConfig().getServerList();
            serverList = new GuiServerList(this, mc, serverX, serverY, 114, 514, 36);
            serverList.setConnectEvent(() -> {
                ModWrapper.pushTempPassword(pwField.getText());
                pwField.setText("");
            });
            serverList.updateOnlineServers(servers);
            pwField = new GuiPasswordField(0, mc.fontRenderer, i, j, 114, 20);
            pwField.setMaxStringLength(32);
            pwField.setEmptyTips("输入密码...");
        }

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, i, j + 24, 56, 20, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, i + 58, j + 24, 56, 20, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(7, i, j + 24 * 2, 114, 20, I18n.format("selectServer.refresh")));
        this.buttonList.add(new GuiButton(0, i, j + 24 * 3, 114, 20, I18n.format("menu.options")));
        this.buttonList.add(langButton = new GuiButton(5, i - 24, j + 24 * 3, 20, 20, ""));
        this.buttonList.add(new GuiButton(8, i, j + 24 * 4, 114, 20, "切换账号"));

        this.buttonList.add(modButton = new GuiButton(6, serverX - 3, j + 24 * 3, serverList.width + 6, 20, I18n.format("fml.menu.mods")));
        this.buttonList.add(new GuiButton(4, serverX - 3, j + 24 * 4, serverList.width + 6, 20, I18n.format("menu.quit")));

        this.mc.setConnectedToRealms(false);

        modUpdateNotification = new NotificationModUpdateScreen(modButton);
        modUpdateNotification.setGuiSize(super.width, super.height);
        modUpdateNotification.initGui();
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.serverList.handleMouseInput();
    }

    /**
     * @author MrXiaoM
     * @reason 重写点击事件
     */
    @Overwrite
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiWorldSelection(this));
        }

        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (button.id == 7) {
            this.serverList.refresh();
        }

        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (button.id == langButton.id) {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (button.id == 8) {
            this.mc.displayGuiScreen(new GuiConfig(this));
        }

        if (button.id == modButton.id) {
            this.mc.displayGuiScreen(new GuiModList(this));
        }

        if (button.id == 4) {
            this.mc.shutdown();
        }
    }

    /**
     * @author MrXiaoM
     * @reason 重写绘制界面
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.panoramaTimer += partialTicks;
        GlStateManager.disableAlpha();
        this.drawBackground(0);
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int j = this.width / 2 - 137;
        int k = this.height / 2 - 90;

        // 标题
        this.mc.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
        this.drawTexturedModalRect(j, k, 0, 0, 155, 44);
        this.drawTexturedModalRect(j + 155, k, 0, 45, 155, 44);

        // 副标题
        this.mc.getTextureManager().bindTexture(field_194400_H);
        drawModalRectWithCustomSizedTexture(j + 88, k + 37, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);

        // SplashText
        GlStateManager.pushMatrix();
        GlStateManager.translate(j + 227, k + 40.0F, 0.0F);
        GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
        float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Minecraft.getSystemTime() % 1000L) / 1000.0F * ((float)Math.PI * 2F)) * 0.1F);
        f = f * 100.0F / (float)(this.fontRenderer.getStringWidth(this.splashText) + 32);
        GlStateManager.scale(f, f, f);
        this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8, -256);
        GlStateManager.popMatrix();

        String s = "我的世界 1.12.2" + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType());

        List<String> brands = Lists.newArrayList(Lists.reverse(FMLCommonHandler.instance().getBrandings(false)));
        brands.add(s);
        for (int line = 0; line < brands.size(); line++) {
            String brd = brands.get(line);
            if (!Strings.isNullOrEmpty(brd)) {
                this.drawString(this.fontRenderer, brd, 2, this.height - (10 + line * (this.fontRenderer.FONT_HEIGHT + 1)), 16777215);
            }
        }

        this.drawString(this.fontRenderer, copyrightString, this.widthCopyrightRest, this.height - 10, -1);

        if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow()) {
            drawRect(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, -1);
        }
        pwField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.color(1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(LANGUAGE_ICON);
        Gui.drawModalRectWithCustomSizedTexture(langButton.x, langButton.y, 0, 0, 20, 20, 20, 20);

        this.serverList.drawScreen(mouseX, mouseY, partialTicks);

        modUpdateNotification.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Inject(at = @At("HEAD"), method = "mouseClicked*", cancellable = true)
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (this.pwField.mouseClicked(mouseX, mouseY, mouseButton)) {
            ci.cancel();
            return;
        }
        if (this.serverList.mouseClicked(mouseX, mouseY, mouseButton)) ci.cancel();
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.serverList.mouseReleased(mouseX, mouseY, state);
    }

    /**
     * @author MrXiaoM
     * @reason 适配密码输入框
     */
    @Overwrite
    protected void keyTyped(char p_73869_1_, int p_73869_2_) throws IOException {
        if (!pwField.textboxKeyTyped(p_73869_1_, p_73869_2_))
            super.keyTyped(p_73869_1_, p_73869_2_);
    }

    /**
     * @author MrXiaoM
     * @reason 适配服务器列表
     */
    @Overwrite
    public void updateScreen() {
        if (this.serverList != null && this.serverList.getOldServerPinger() != null)
            this.serverList.getOldServerPinger().pingPendingNetworks();
    }

    /**
     * @author MrXiaoM
     * @reason 适配服务器列表
     */
    @Overwrite
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        if (this.serverList != null && this.serverList.getOldServerPinger() != null)
            this.serverList.getOldServerPinger().clearPendingNetworks();
    }
}
