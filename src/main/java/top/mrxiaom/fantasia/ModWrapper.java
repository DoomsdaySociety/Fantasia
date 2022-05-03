package top.mrxiaom.fantasia;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
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

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModWrapper extends DummyModContainer {
    private static Logger logger;
    private static final ResourceLocation resBg = new ResourceLocation("fantasia", "textures/gui/background.png");
    private static ModWrapper instance;
    private File configPath;
    private MainMenuConfig mainMenuConfig;
    private ChatConfig chatConfig;
    public static ModWrapper getInstance() {
        return instance;
    }

    public MainMenuConfig getMainMenuConfig() { return mainMenuConfig; }
    public ChatConfig getChatConfig() { return chatConfig; }

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
        configPath = new File(FMLPlugin.getMcLocation(), meta.modId);
        reloadConfig();
    }

    public void reloadConfig() {
        if (this.chatConfig == null) this.chatConfig = new ChatConfig(new File(configPath, "chat.json"));
        if (this.mainMenuConfig == null) this.mainMenuConfig = new MainMenuConfig(new File(configPath, "mainmenu.json"));
        this.chatConfig.reloadConfig();
        this.mainMenuConfig.reloadConfig();
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
    @Subscribe
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * 处理 ActionMessage
     * @param chatComponent 原内容
     * @return 修改后输出的 ActionMessage，null 为拦截内容不输出
     */
    public static ITextComponent onActionMessageReceived(ITextComponent chatComponent) {
        return chatComponent;
    }

    /**
     * 处理聊天
     * @param text 原内容
     * @param type 原聊天类型
     * @return 修改后输出的聊天内容，null 为拦截内容不输出
     */
    public static ITextComponent onChatReceived(ITextComponent text, ChatType type) {
        String unformatted = text.getUnformattedText().replace("§r", "");
        StringBuilder stringbuilder = new StringBuilder();
        Map<String, ITextComponent> special = new HashMap<>();
        for (ITextComponent itextcomponent : text)
        {
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

        ChatConfig chatConfig = instance.chatConfig;
        for (String s : chatConfig.ignoredKeywords) {
            if (unformatted.contains(s)) return null;
        }
        for (String s : chatConfig.ignoredPatterns) {
            if (Pattern.matches(s, unformatted)) return null;
        }
        Matcher m = chatConfig.compiledChatPattern.matcher(formatted);
        if (m.find()) {
            String prefix = m.group(chatConfig.prefixGroup);
            String name = m.group(chatConfig.nameGroup);
            String suffix = m.group(chatConfig.suffixGroup);
            suffix = chatConfig.suffixes.getOrDefault(
                    suffix.contains("§b§l") ? "vip" :
                    (suffix.contains("§6§l") ? "svip" :
                    (suffix.contains("§c§l") ? "mvp" : "none")), suffix);
            String msg = m.group(m.groupCount());
            if (!chatConfig.chatReplaceFormat.isEmpty()) {
                ITextComponent iMsg = new TextComponentString(chatConfig.chatReplaceFormat
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

    public VersionRange acceptableMinecraftVersionRange()
    {
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
        if (instance.mainMenuConfig.isParallax) {
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
