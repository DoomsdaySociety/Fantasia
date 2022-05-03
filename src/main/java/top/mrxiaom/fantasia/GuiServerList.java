package top.mrxiaom.fantasia;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.util.List;

public class GuiServerList extends GuiListExtended {
    private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
    private final List<GuiServerEntry> serverListInternet = Lists.newArrayList();
    private int selectedSlotIndex = 0;
    public final ServerPinger serverPinger = new ServerPinger();
    private String hoveringText = null;
    public final GuiScreen parent;
    public int x;
    public int y;
    public GuiServerList(GuiScreen parent, Minecraft mcIn, int x, int y, int widthIn, int heightIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, y, y + heightIn, slotHeightIn);
        setDimensions(widthIn, heightIn, x, y);
        this.showSelectionBox = false;
        this.parent = parent;
        this.hasListHeader = false;
        this.refresh();
    }

    public void refresh() {
        for (GuiServerEntry entry : serverListInternet) {
            entry.ping();
        }
    }

    @Override
    public void setDimensions(int widthIn, int heightIn, int x, int y) {
        this.width = widthIn;
        this.height = slotHeight * getSize() + 14;
        this.x = x;
        this.y = y;
        this.top = y + 10;
        this.bottom = y + this.height + 8;
        this.left = x;
        this.right = x + widthIn;
    }

    @Override
    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {

    }

    @Override
    protected void drawContainerBackground(Tessellator tessellator) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.hoveringText = null;

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        int l1 = x - 4;
        int i2 = y - 4;
        int i = width + 8;
        int k = height + 8;

        drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864, 0);
        drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864, 0);
        drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864, 0);
        drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864, 0);
        drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864, 0);
        /*
        drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847, 0);
        drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847, 0);
        drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415, 0);
        drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847, 0);
*/
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();

        mc.fontRenderer.drawStringWithShadow("选择服务器", x, y, 0xFFFFFF);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.translate(x + width - 16, y - 2, 400);
        this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, showSelectionBox ? 96.0F : 64.0F, (showSelectionBox ? 0f : 16f) + ((mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + 10) ? 32.0F : 0.0F), 16, 16, 256.0F, 256.0F);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
        if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + 10) {
            this.hoveringText = showSelectionBox ? "点击收起服务器列表" : "点击展开服务器列表";
        }
        if (this.hoveringText != null) {
            parent.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
        }
    }


    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
        if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + 10) {
            showSelectionBox = ! showSelectionBox;
            setDimensions(width, height, x, y);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseEvent);
    }

    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor, int zLevel)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }


    public void setHoveringText(String hoveringText)
    {
        this.hoveringText = hoveringText;
    }

    public String getHoveringText() { return this.hoveringText; }

    public ServerPinger getOldServerPinger() { return serverPinger; }

    public GuiServerEntry getListEntry(int index)
    {
        return this.serverListInternet.get(showSelectionBox ? index : selectedSlotIndex);
    }

    public boolean isShowSelectionBox() { return showSelectionBox; }

    public void setShowSelectionBox(boolean showSelectionBox) { this.showSelectionBox = showSelectionBox; }

    protected int getSize()
    {
        return showSelectionBox ? this.serverListInternet.size() : 1;
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn)
    {
        showSelectionBox = false;
        setDimensions(width, slotHeight * getSize() + 4, x, y);
        ModWrapper.getInstance().getMainMenuConfig().selected = getListEntry(this.selectedSlotIndex = selectedSlotIndexIn).getServerData().serverName;
        ModWrapper.getInstance().getMainMenuConfig().saveConfig();
    }

    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedSlotIndex;
    }

    public int getSelected()
    {
        return this.selectedSlotIndex;
    }

    public void updateOnlineServers(List<ServerData> p_148195_1_)
    {
        this.serverListInternet.clear();

        for (int i = 0; i < p_148195_1_.size(); ++i)
        {
            if (p_148195_1_.get(i).serverName.equals(ModWrapper.getInstance().getMainMenuConfig().selected)) {
                selectedSlotIndex = i;
            }
            this.serverListInternet.add(new GuiServerEntry(this, p_148195_1_.get(i)));
        }
        setDimensions(width, height, x, y);
    }

    private void connectToServer(ServerData server)
    {
        FMLClientHandler.instance().connectToServer(parent, server);
    }

    public void connectToSelected() {
        this.connectToServer(this.getListEntry(this.getSelected()).getServerData());
    }

    protected int getScrollBarX()
    {
        return this.x + this.width;
    }

    public int getListWidth()
    {
        return this.width;
    }

    public void connectTo(GuiServerEntry guiServerEntry) {
        connectToServer(guiServerEntry.getServerData());
    }
}
