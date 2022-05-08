package top.mrxiaom.fantasia;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import top.mrxiaom.fantasia.config.ChatConfig;
import top.mrxiaom.fantasia.config.HUDConfig;
import top.mrxiaom.fantasia.config.MainMenuConfig;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModWrapper extends DummyModContainer {
    private static Logger logger;
    private static final ResourceLocation resBg = new ResourceLocation("fantasia", "textures/gui/background.png");
    private static ModWrapper instance;
    private static MainMenuConfig mainMenuConfig;
    private static ChatConfig chatConfig;
    private static HUDConfig hudConfig;
    private static String tempPW = "";
    public static ModWrapper getInstance() {
        return instance;
    }

    public static MainMenuConfig getMainMenuConfig() {
        return mainMenuConfig;
    }

    public static ChatConfig getChatConfig() {
        return chatConfig;
    }

    public static HUDConfig getHudConfig() {
        return hudConfig;
    }

    public static void pushTempPassword(String pw) {
        tempPW = pw;
    }

    public static int getMouseX(ScaledResolution scale) {
        int mcWidth = scale.getScaledWidth();
        return org.lwjgl.input.Mouse.getX() * mcWidth / Minecraft.getMinecraft().displayWidth;
    }

    public static int getMouseY(ScaledResolution scale) {
        int mcHeight = scale.getScaledHeight();
        return mcHeight - org.lwjgl.input.Mouse.getY() * mcHeight / Minecraft.getMinecraft().displayHeight - 1;
    }

    public static Logger getLogger() {
        return logger;
    }

    public ModWrapper() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();
        meta.modId = "fantasia";
        meta.name = "幻想曲";
        meta.version = "1.0";
        meta.logoFile = "logo.png";
        meta.description = "适用于零都市服务器的辅助Mod";
        meta.authorList = Arrays.asList("MrXiaoM", "AnonymousTech");
        meta.credits = "末日社、企鹅物流";
        meta.url = "https://github.com/DoomsdaySociety/Fantasia";
        logger = LogManager.getLogger("Fantasia");
        instance = this;
        FMLClientHandler.instance().addModAsResource(this);
    }

    public static void reloadConfig() {
        if (chatConfig == null) chatConfig = new ChatConfig(new File(FMLPlugin.getConfigPath(), "chat.json"));
        if (mainMenuConfig == null) mainMenuConfig = new MainMenuConfig(new File(FMLPlugin.getConfigPath(), "mainmenu.json"));
        if (hudConfig == null) hudConfig = new HUDConfig(new File(FMLPlugin.getConfigPath(), "hud.json"));
        chatConfig.reloadConfig();
        mainMenuConfig.reloadConfig();
        hudConfig.reloadConfig();
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.HEALTH)
                || event.getType().equals(RenderGameOverlayEvent.ElementType.FOOD)
                || event.getType().equals(RenderGameOverlayEvent.ElementType.AIR))
            event.setCanceled(true);
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.HEALTH)) drawStats(event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight());
    }

    @SubscribeEvent
    public void onRenderGameText(RenderGameOverlayEvent.Text event) {
        // 展示框物品显示
        // 继承自 都市小工具
        Minecraft mc = Minecraft.getMinecraft();
        if (hudConfig.isShowFrameItem && mc.pointedEntity instanceof EntityItemFrame) {
            EntityItemFrame frame = (EntityItemFrame) mc.pointedEntity;
            ItemStack item = frame.getDisplayedItem();
            if (!item.isEmpty()) {
                ScaledResolution scaled = new ScaledResolution(mc);
                int width = scaled.getScaledWidth();
                int height = scaled.getScaledHeight();
                int x = width / 2 + 5;
                int y = height / 2 + 10;
                List<String> textLines = item.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

                GlStateManager.pushMatrix();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableDepth();
                int i = 0;

                for (String s : textLines) {
                    int j = mc.fontRenderer.getStringWidth(s);

                    if (j > i) {
                        i = j;
                    }
                }

                int l1 = x + 12;
                int i2 = y - 12;
                int k = 8;

                if (textLines.size() > 1) {
                    k += 2 + (textLines.size() - 1) * 10;
                }

                if (l1 + i > width) {
                    l1 -= 28 + i;
                }

                if (i2 + k + 6 > height) {
                    i2 = height - k - 6;
                }

                int l = 1342177280;
                Utils.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l, 300.0F);
                Utils.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l, 300.0F);
                Utils.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l, 300.0F);
                Utils.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l, 300.0F);
                Utils.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l, 300.0F);
                int i1 = (int) 2415984639L;
                int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
                Utils.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1, 300.0F);
                Utils.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1, 300.0F);
                Utils.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1, 300.0F);
                Utils.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1, 300.0F);


                int k1 = 0;
                for (String str1 : textLines) {
                    mc.fontRenderer.drawStringWithShadow(str1, l1, i2, -1);

                    if (k1 == 0) {
                        i2 += 2;
                    }

                    i2 += mc.fontRenderer.FONT_HEIGHT;
                    k1++;
                }

                GlStateManager.enableDepth();
                GlStateManager.enableRescaleNormal();
                GlStateManager.popMatrix();
            }
        }
    }

    public static boolean drawStats(int width, int height) {
        if (hudConfig.useFancyHUD) {
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = mc.fontRenderer;
            if (mc.getRenderViewEntity() instanceof EntityPlayer) {
                DecimalFormat df = new DecimalFormat("0.0");
                int x = width / 2 - 85;
                int y = height - 68;
                EntityPlayer entityplayer = (EntityPlayer)mc.getRenderViewEntity();
                String healthString = "HP: " + df.format(entityplayer.getHealth()) + " / " + df.format(entityplayer.getMaxHealth());
                float health = entityplayer.getHealth() / entityplayer.getMaxHealth();
                FoodStats foodStats = entityplayer.getFoodStats();
                float food = foodStats.getFoodLevel() / 20f;
                // 画背景
                Utils.fillRect(x, y, x + 170, y + 24, 60, 23, 23, 23);
                Utils.drawRect(x, y, x + 170, y + 24,-1, 255, 0, 245, 245);
                // 画氧气值
                if (entityplayer.isInsideOfMaterial(Material.WATER)) {
                    float air = mc.player.getAir() / 300f;
                    Utils.fillRect(x, y, x + air * 170, y + 24, 128, 0, 245, 245);
                }
                // 画头像
                ResourceLocation skin = mc.player.getLocationSkin();
                GlStateManager.color(1, 1, 1);
                mc.getTextureManager().bindTexture(skin);
                // 8,8
                Gui.drawModalRectWithCustomSizedTexture(x + 4, y + 4, 16, 16,
                        16, 16, 128, 128);
                // 40,8
                Gui.drawModalRectWithCustomSizedTexture(x + 4, y + 4, 80, 16,
                        16, 16, 128, 128);
                font.drawStringWithShadow("§e§l" + entityplayer.getDisplayNameString(), x + 24, y + 3, 0xFFFFFF);
                // 画血条
                Utils.fillRect(x + 24, y +12, x + 166, y + 20, 90, 150, 150, 150);
                Utils.fillRect(x + 24, y +12, x + 24 + health * 142, y + 20, 128, 245, 216, 0);
                font.drawStringWithShadow(healthString, x + 166 - font.getStringWidth(healthString), y + 3, 0xFFFFFF);
                // 画饱食度
                Utils.fillRect(x + 24, y + 18, x + 24 + food * 142, y + 20, 128, 245, 106, 0);
                mc.getTextureManager().bindTexture(Gui.ICONS);
                GlStateManager.color(1, 1, 1);
            }
            return true;
        }
        return false;
    }

    /**
     * 处理 ActionMessage
     *
     * @param chatComponent 原内容
     * @return 修改后输出的 ActionMessage，null 为拦截内容不输出
     */
    public static ITextComponent onActionMessageReceived(ITextComponent chatComponent) {
        return chatComponent;
    }

    /**
     * 处理聊天
     *
     * @param text 原内容
     * @param type 原聊天类型
     * @return 修改后输出的聊天内容，null 为拦截内容不输出
     */
    public static ITextComponent onChatReceived(ITextComponent text, ChatType type) {
        if (!tempPW.isEmpty()) {
            String pw = tempPW;
            tempPW = "";
            Minecraft.getMinecraft().player.sendChatMessage("/login " + pw);
        }
        String unformatted = text.getUnformattedText().replace("§r", "");
        for (String keyword : chatConfig.ignoredKeywords) {
            if (unformatted.contains(keyword)) return null;
        }
        for (String pattern : chatConfig.ignoredPatterns) {
            try {
                if (Pattern.matches(pattern, unformatted)) return null;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        StringBuilder stringbuilder = new StringBuilder();
        Map<String, ITextComponent> special = new HashMap<>();
        for (ITextComponent itextcomponent : text) {
            String s = itextcomponent.getUnformattedComponentText();
            if (!s.isEmpty()) {
                s = itextcomponent.getStyle().getFormattingCode() + s;
                if (itextcomponent.getStyle().getHoverEvent() != null || itextcomponent.getStyle().getClickEvent() != null) {
                    special.put(s, itextcomponent);
                }
                stringbuilder.append(s);
            }
        }
        String formatted = stringbuilder.toString();

        Matcher m = chatConfig.compiledChatPattern.matcher(formatted);
        if (m.find()) {
            // tag: 全服喊话标识
            String tag = m.group(chatConfig.tagGroup);
            // prefix: 称号
            String prefix = m.group(chatConfig.prefixGroup);
            // name: 玩家名
            String name = m.group(chatConfig.nameGroup);
            if (chatConfig.ignoredPlayers.contains(name)) return null;
            // suffix: 后缀(如VIP)
            String suffix = m.group(chatConfig.suffixGroup);
            suffix = chatConfig.suffixes.getOrDefault(
                    suffix.contains("§b§l") ? "vip" :
                            (suffix.contains("§6§l") ? "svip" :
                                    (suffix.contains("§c§l") ? "mvp" : "none")), suffix);
            String msg = m.group(m.groupCount());
            if (!chatConfig.chatReplaceFormat.isEmpty()) {
                ITextComponent iMsg = new TextComponentString(chatConfig.specialFormats.getOrDefault(name, chatConfig.chatReplaceFormat)
                        .replace("$tag", tag)
                        .replace("$prefix", prefix)
                        .replace("$player", name)
                        .replace("$suffix", suffix));
                for (String key : special.keySet()) {
                    if (msg.contains(key)) {
                        iMsg.appendSibling(new TextComponentString(msg.substring(0, msg.indexOf(key))));
                        iMsg.appendSibling(special.get(key));
                        msg = msg.substring(msg.indexOf(key) + key.length());
                    }
                }
                iMsg.appendSibling(new TextComponentString(msg));
                return iMsg;
            }
        }
        return text;
    }

    public VersionRange acceptableMinecraftVersionRange() {
        return VersionParser.parseRange("[1.12,1.12.2]");
    }

    public Class<?> getCustomResourcePackClass() {
        return FMLFileResourcePack.class;
    }

    public File getSource() {
        return FMLPlugin.getCoremodLocation();
    }

    public static void updateTitle() {
        Minecraft mc = Minecraft.getMinecraft();

        Display.setTitle("我的世界 1.12.2 | Fantasia Mod | 零都市 | " + mc.getSession().getUsername());
    }

    public static void drawBackground() {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int mcWidth = scale.getScaledWidth();
        int mcHeight = scale.getScaledHeight();
        double imgScale = 16.0d / 9.0d;
        // 图片横屏或竖屏，窗口横屏
        double width = imgScale * mcHeight;
        double height = mcHeight;
        if (width < mcWidth) {
            width = mcWidth;
            height = mcWidth / imgScale;
        }
        if (height < mcHeight) {
            width = imgScale * mcHeight;
            height = mcHeight;
        }
        double x = -(width - (double) mcWidth) / 2.0d;
        double y = -(height - (double) mcHeight) / 2.0d;
        if (mainMenuConfig.isParallax) {
            double aW = (width * 0.025d);
            double aH = (height * 0.025d);
            width *= 1.1d;
            height *= 1.1d;
            x += ((double) getMouseX(scale) / (double) mcWidth - 1.0d) * aW;
            y += ((double) getMouseY(scale) / (double) mcHeight - 1.0d) * aH;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(resBg);
        double f = 1.0d / 1920.0d;
        double f1 = 1.0d / 1080.0d;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0D).tex(0, 1080.0d * f1).endVertex();
        bufferbuilder.pos(x + width, (y + height), 0.0D).tex(1920.0d * f, 1080.0d * f1).endVertex();
        bufferbuilder.pos(x + width, y, 0.0D).tex(1920.0d * f, 0).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(0, 0).endVertex();
        tessellator.draw();
    }
}
