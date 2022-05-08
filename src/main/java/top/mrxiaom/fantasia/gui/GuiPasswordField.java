package top.mrxiaom.fantasia.gui;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;

/**
 * @see net.minecraft.client.gui.GuiTextField
 */
public class GuiPasswordField extends Gui {
    private final int id;
    private final FontRenderer fontRenderer;
    public int x;
    public int y;
    public int width;
    public int height;
    private String text = "";
    private String emptyTips = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    private boolean isFocused;
    private boolean isEnabled = true;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;
    private boolean visible = true;
    private GuiPageButtonList.GuiResponder guiResponder;
    private Predicate<String> validator = Predicates.alwaysTrue();

    public GuiPasswordField(int p_i45542_1_, FontRenderer p_i45542_2_, int p_i45542_3_, int p_i45542_4_, int p_i45542_5_, int p_i45542_6_) {
        this.id = p_i45542_1_;
        this.fontRenderer = p_i45542_2_;
        this.x = p_i45542_3_;
        this.y = p_i45542_4_;
        this.width = p_i45542_5_;
        this.height = p_i45542_6_;
    }

    public String getEmptyTips() {
        return emptyTips;
    }

    public void setEmptyTips(String emptyTips) {
        this.emptyTips = emptyTips;
    }

    public void setDimensions(int x, int y) {
        setDimensions(x, y, width, height);
    }

    public void setDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setGuiResponder(GuiPageButtonList.GuiResponder p_175207_1_) {
        this.guiResponder = p_175207_1_;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void setText(String p_146180_1_) {
        if (this.validator.apply(p_146180_1_)) {
            if (p_146180_1_.length() > this.maxStringLength) {
                this.text = p_146180_1_.substring(0, this.maxStringLength);
            } else {
                this.text = p_146180_1_;
            }

            this.setCursorPositionEnd();
        }
    }

    public String getText() {
        return this.text;
    }

    public void setValidator(Predicate<String> p_175205_1_) {
        this.validator = p_175205_1_;
    }

    public void writeText(String p_146191_1_) {
        String lvt_2_1_ = "";
        String lvt_3_1_ = ChatAllowedCharacters.filterAllowedCharacters(p_146191_1_);
        int lvt_4_1_ = Math.min(this.cursorPosition, this.selectionEnd);
        int lvt_5_1_ = Math.max(this.cursorPosition, this.selectionEnd);
        int lvt_6_1_ = this.maxStringLength - this.text.length() - (lvt_4_1_ - lvt_5_1_);
        if (!this.text.isEmpty()) {
            lvt_2_1_ = lvt_2_1_ + this.text.substring(0, lvt_4_1_);
        }

        int lvt_7_2_;
        if (lvt_6_1_ < lvt_3_1_.length()) {
            lvt_2_1_ = lvt_2_1_ + lvt_3_1_.substring(0, lvt_6_1_);
            lvt_7_2_ = lvt_6_1_;
        } else {
            lvt_2_1_ = lvt_2_1_ + lvt_3_1_;
            lvt_7_2_ = lvt_3_1_.length();
        }

        if (!this.text.isEmpty() && lvt_5_1_ < this.text.length()) {
            lvt_2_1_ = lvt_2_1_ + this.text.substring(lvt_5_1_);
        }

        if (this.validator.apply(lvt_2_1_)) {
            this.text = lvt_2_1_;
            this.moveCursorBy(lvt_4_1_ - this.selectionEnd + lvt_7_2_);
            this.setResponderEntryValue(this.id, this.text);
        }
    }

    public void setResponderEntryValue(int p_190516_1_, String p_190516_2_) {
        if (this.guiResponder != null) {
            this.guiResponder.setEntryValue(p_190516_1_, p_190516_2_);
        }

    }

    public void deleteWords(int p_146177_1_) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(p_146177_1_) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int p_146175_1_) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean lvt_2_1_ = p_146175_1_ < 0;
                int lvt_3_1_ = lvt_2_1_ ? this.cursorPosition + p_146175_1_ : this.cursorPosition;
                int lvt_4_1_ = lvt_2_1_ ? this.cursorPosition : this.cursorPosition + p_146175_1_;
                String lvt_5_1_ = "";
                if (lvt_3_1_ >= 0) {
                    lvt_5_1_ = this.text.substring(0, lvt_3_1_);
                }

                if (lvt_4_1_ < this.text.length()) {
                    lvt_5_1_ = lvt_5_1_ + this.text.substring(lvt_4_1_);
                }

                if (this.validator.apply(lvt_5_1_)) {
                    this.text = lvt_5_1_;
                    if (lvt_2_1_) {
                        this.moveCursorBy(p_146175_1_);
                    }

                    this.setResponderEntryValue(this.id, this.text);
                }
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public int getNthWordFromCursor(int p_146187_1_) {
        return this.getNthWordFromPos(p_146187_1_, this.getCursorPosition());
    }

    public int getNthWordFromPos(int p_146183_1_, int p_146183_2_) {
        return this.getNthWordFromPosWS(p_146183_1_, p_146183_2_, true);
    }

    public int getNthWordFromPosWS(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
        int lvt_4_1_ = p_146197_2_;
        boolean lvt_5_1_ = p_146197_1_ < 0;
        int lvt_6_1_ = Math.abs(p_146197_1_);

        for (int lvt_7_1_ = 0; lvt_7_1_ < lvt_6_1_; ++lvt_7_1_) {
            if (!lvt_5_1_) {
                int lvt_8_1_ = this.text.length();
                lvt_4_1_ = this.text.indexOf(32, lvt_4_1_);
                if (lvt_4_1_ == -1) {
                    lvt_4_1_ = lvt_8_1_;
                } else {
                    while (p_146197_3_ && lvt_4_1_ < lvt_8_1_ && this.text.charAt(lvt_4_1_) == ' ') {
                        ++lvt_4_1_;
                    }
                }
            } else {
                while (p_146197_3_ && lvt_4_1_ > 0 && this.text.charAt(lvt_4_1_ - 1) == ' ') {
                    --lvt_4_1_;
                }

                while (lvt_4_1_ > 0 && this.text.charAt(lvt_4_1_ - 1) != ' ') {
                    --lvt_4_1_;
                }
            }
        }

        return lvt_4_1_;
    }

    public void moveCursorBy(int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    public void setCursorPosition(int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        int lvt_2_1_ = this.text.length();
        this.cursorPosition = MathHelper.clamp(this.cursorPosition, 0, lvt_2_1_);
        this.selectionEnd = this.cursorPosition;
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_) {
        if (!this.isFocused) {
            return false;
        } else {
            switch (p_146201_2_) {
                case 14:
                    if (GuiScreen.isCtrlKeyDown()) {
                        if (this.isEnabled) {
                            this.deleteWords(-1);
                        }
                    } else if (this.isEnabled) {
                        this.deleteFromCursor(-1);
                    }

                    return true;
                case 199:
                    if (!GuiScreen.isShiftKeyDown()) {
                        this.setCursorPositionZero();
                    }

                    return true;
                case 203:
                    if (!GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(-1));
                    } else {
                        this.moveCursorBy(-1);
                    }

                    return true;
                case 205:
                    if (!GuiScreen.isShiftKeyDown() && GuiScreen.isCtrlKeyDown()) {
                        this.setCursorPosition(this.getNthWordFromCursor(1));
                    } else {
                        this.moveCursorBy(1);
                    }

                    return true;
                case 207:
                    if (!GuiScreen.isShiftKeyDown()) {
                        this.setCursorPositionEnd();
                    }

                    return true;
                case 211:
                    if (GuiScreen.isCtrlKeyDown()) {
                        if (this.isEnabled) {
                            this.deleteWords(1);
                        }
                    } else if (this.isEnabled) {
                        this.deleteFromCursor(1);
                    }

                    return true;
                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(p_146201_1_)) {
                        if (this.isEnabled) {
                            this.writeText(Character.toString(p_146201_1_));
                        }

                        return true;
                    } else {
                        return false;
                    }
            }
        }
    }

    public boolean mouseClicked(int p_146192_1_, int p_146192_2_, int p_146192_3_) {
        boolean lvt_4_1_ = p_146192_1_ >= this.x && p_146192_1_ < this.x + this.width && p_146192_2_ >= this.y && p_146192_2_ < this.y + this.height;
        if (this.canLoseFocus) {
            this.setFocused(lvt_4_1_);
        }

        if (this.isFocused && lvt_4_1_ && p_146192_3_ == 0) {
            int lvt_5_1_ = p_146192_1_ - this.x;
            if (this.enableBackgroundDrawing) {
                lvt_5_1_ -= 4;
            }

            String text = "";
            for (int i = 0; i < this.text.length(); i++) text += "*";
            String lvt_6_1_ = this.fontRenderer.trimStringToWidth(text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(lvt_6_1_, lvt_5_1_).length() + this.lineScrollOffset);
            return true;
        } else {
            return false;
        }
    }

    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
            }

            int lvt_1_1_ = this.isEnabled ? this.enabledColor : this.disabledColor;
            int lvt_2_1_ = this.cursorPosition - this.lineScrollOffset;
            int lvt_3_1_ = this.selectionEnd - this.lineScrollOffset;
            String text = "";
            for (int i = 0; i < this.text.length(); i++) text += "*";
            String lvt_4_1_ = this.fontRenderer.trimStringToWidth(text.substring(this.lineScrollOffset), this.getWidth());
            boolean lvt_5_1_ = lvt_2_1_ >= 0 && lvt_2_1_ <= lvt_4_1_.length();
            boolean lvt_6_1_ = this.isFocused && this.cursorCounter / 6 % 2 == 0 && lvt_5_1_;
            int lvt_7_1_ = this.enableBackgroundDrawing ? this.x + 4 : this.x;
            int lvt_8_1_ = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
            int lvt_9_1_ = lvt_7_1_;
            if (lvt_3_1_ > lvt_4_1_.length()) {
                lvt_3_1_ = lvt_4_1_.length();
            }

            if (!lvt_4_1_.isEmpty()) {
                String lvt_10_1_ = lvt_5_1_ ? lvt_4_1_.substring(0, lvt_2_1_) : lvt_4_1_;
                lvt_9_1_ = this.fontRenderer.drawStringWithShadow(lvt_10_1_, (float) lvt_7_1_, (float) lvt_8_1_, lvt_1_1_);
            }

            boolean lvt_10_2_ = this.cursorPosition < text.length() || text.length() >= this.getMaxStringLength();
            int lvt_11_1_ = lvt_9_1_;
            if (!lvt_5_1_) {
                lvt_11_1_ = lvt_2_1_ > 0 ? lvt_7_1_ + this.width : lvt_7_1_;
            } else if (lvt_10_2_) {
                lvt_11_1_ = lvt_9_1_ - 1;
                --lvt_9_1_;
            }

            if (!lvt_4_1_.isEmpty() && lvt_5_1_ && lvt_2_1_ < lvt_4_1_.length()) {
                lvt_9_1_ = this.fontRenderer.drawStringWithShadow(lvt_4_1_.substring(lvt_2_1_), (float) lvt_9_1_, (float) lvt_8_1_, lvt_1_1_);
            }

            if (this.text.isEmpty()) {
                this.fontRenderer.drawStringWithShadow(emptyTips, (float) lvt_9_1_, (float) lvt_8_1_, this.disabledColor);
            }

            if (lvt_6_1_) {
                if (lvt_10_2_) {
                    Gui.drawRect(lvt_11_1_, lvt_8_1_ - 1, lvt_11_1_ + 1, lvt_8_1_ + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
                } else {
                    this.fontRenderer.drawStringWithShadow("_", (float) lvt_11_1_, (float) lvt_8_1_, lvt_1_1_);
                }
            }

            if (lvt_3_1_ != lvt_2_1_) {
                int lvt_12_1_ = lvt_7_1_ + this.fontRenderer.getStringWidth(lvt_4_1_.substring(0, lvt_3_1_));
                this.drawSelectionBox(lvt_11_1_, lvt_8_1_ - 1, lvt_12_1_ - 1, lvt_8_1_ + 1 + this.fontRenderer.FONT_HEIGHT);
            }

        }
    }

    private void drawSelectionBox(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {
        int lvt_5_2_;
        if (p_146188_1_ < p_146188_3_) {
            lvt_5_2_ = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = lvt_5_2_;
        }

        if (p_146188_2_ < p_146188_4_) {
            lvt_5_2_ = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = lvt_5_2_;
        }

        if (p_146188_3_ > this.x + this.width) {
            p_146188_3_ = this.x + this.width;
        }

        if (p_146188_1_ > this.x + this.width) {
            p_146188_1_ = this.x + this.width;
        }

        Tessellator lvt_5_3_ = Tessellator.getInstance();
        BufferBuilder lvt_6_1_ = lvt_5_3_.getBuffer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        lvt_6_1_.begin(7, DefaultVertexFormats.POSITION);
        lvt_6_1_.pos(p_146188_1_, p_146188_4_, 0.0D).endVertex();
        lvt_6_1_.pos(p_146188_3_, p_146188_4_, 0.0D).endVertex();
        lvt_6_1_.pos(p_146188_3_, p_146188_2_, 0.0D).endVertex();
        lvt_6_1_.pos(p_146188_1_, p_146188_2_, 0.0D).endVertex();
        lvt_5_3_.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int p_146203_1_) {
        this.maxStringLength = p_146203_1_;
        if (this.text.length() > p_146203_1_) {
            this.text = this.text.substring(0, p_146203_1_);
        }

    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public boolean getEnableBackgroundDrawing() {
        return this.enableBackgroundDrawing;
    }

    public void setEnableBackgroundDrawing(boolean p_146185_1_) {
        this.enableBackgroundDrawing = p_146185_1_;
    }

    public void setTextColor(int p_146193_1_) {
        this.enabledColor = p_146193_1_;
    }

    public void setDisabledTextColour(int p_146204_1_) {
        this.disabledColor = p_146204_1_;
    }

    public void setFocused(boolean p_146195_1_) {
        if (p_146195_1_ && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = p_146195_1_;
        if (Minecraft.getMinecraft().currentScreen != null) {
            Minecraft.getMinecraft().currentScreen.setFocused(p_146195_1_);
        }
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void setEnabled(boolean p_146184_1_) {
        this.isEnabled = p_146184_1_;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public int getWidth() {
        return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    public void setCanLoseFocus(boolean p_146205_1_) {
        this.canLoseFocus = p_146205_1_;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean p_146189_1_) {
        this.visible = p_146189_1_;
    }
}
