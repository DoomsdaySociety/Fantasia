package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mrxiaom.fantasia.FMLPlugin;

import java.util.Timer;
import java.util.TimerTask;

@Mixin(GuiMerchant.class)
public abstract class MixinGuiMerchant extends GuiContainer {

    @Mutable
    @Shadow
    @Final
    private IMerchant merchant;

    @Mutable
    @Shadow
    @Final
    private ITextComponent chatComponent;

    @Shadow
    private int selectedMerchantRecipe;
    private final Timer timer = new Timer();

    public MixinGuiMerchant(InventoryPlayer inv, IMerchant merchant, World worldIn) {
        super(new ContainerMerchant(inv, merchant, worldIn));
        this.merchant = merchant;
        this.chatComponent = merchant.getDisplayName();
    }

    @Inject(at = @At("RETURN"), method = "initGui")
    public void initGui(CallbackInfo ci) {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.buttonList.add(new GuiButton(6, i + 120 + 27, j + 53 - 1, 50, 20, "一键兑换"));
    }

    @Inject(at = @At("RETURN"), method = "actionPerformed")
    protected void actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 6) {
            MerchantRecipeList recipes = merchant.getRecipes(mc.player);
            if (recipes != null && this.selectedMerchantRecipe >= 0 && this.selectedMerchantRecipe < recipes.size()) {
                doMerchantCycle(recipes.get(this.selectedMerchantRecipe));
            }
        }
    }

    public void doMerchantCycle(MerchantRecipe recipe) {
        boolean slotOnePresent = inventorySlots.getSlot(0).getHasStack();
        boolean slotTwoPresent = inventorySlots.getSlot(1).getHasStack();
        ItemStack one = recipe.getItemToBuy();
        ItemStack two = recipe.getSecondItemToBuy();
        // System.out.println(one.getDisplayName() + " " + one.getItem().getUnlocalizedName() + "*" + one.getCount());
        // System.out.println(two.getDisplayName() + " " + two.getItem().getUnlocalizedName() + "*" + two.getCount());
        doMerchantCycle(recipe, one, two, slotOnePresent, slotTwoPresent);
    }

    public void doMerchantCycle(MerchantRecipe recipe, ItemStack one, ItemStack two, boolean slotOnePresent, boolean slotTwoPresent) {
        ItemStack tempOne = one.copy();
        ItemStack tempTwo = two.copy();
        int size = inventorySlots.inventorySlots.size();
        // System.out.println("界面大小: " + size);
        for (int i = 0; i < size; i++) {
            Slot slot = inventorySlots.getSlot(i);
            ItemStack item = slot.getStack();
            if (item.isEmpty()) continue;
            // System.out.println("第" + i + "格 " + item.getDisplayName() + " " + item.getItem().getUnlocalizedName() + "*" + item.getCount());
            if (!tempOne.isEmpty() && item.isItemEqual(tempOne) && tempOne.getCount() <= item.getCount()) {
                inventorySlots.slotClick(i, 0, ClickType.PICKUP, mc.player);
                inventorySlots.slotClick(0, 0, ClickType.PICKUP, mc.player);
                if (slotOnePresent)
                    inventorySlots.slotClick(i, 0, ClickType.PICKUP, mc.player);
                tempOne = ItemStack.EMPTY;
            } else if (!tempTwo.isEmpty() && item.isItemEqual(tempTwo) && tempTwo.getCount() <= item.getCount()) {
                inventorySlots.slotClick(i, 0, ClickType.PICKUP, mc.player);
                inventorySlots.slotClick(1, 0, ClickType.PICKUP, mc.player);
                if (slotTwoPresent)
                    inventorySlots.slotClick(i, 0, ClickType.PICKUP, mc.player);
                tempTwo = ItemStack.EMPTY;
            }
            // 物品都放入完成后
            if (tempOne.isEmpty() && tempTwo.isEmpty()) {
                // 兑换
                inventorySlots.slotClick(2, 0, ClickType.QUICK_MOVE, mc.player);
                // 撤走物品
                inventorySlots.slotClick(0, 0, ClickType.QUICK_MOVE, mc.player);
                inventorySlots.slotClick(1, 0, ClickType.QUICK_MOVE, mc.player);
                if (!recipe.isRecipeDisabled())
                    // 进行下一轮兑换
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            doMerchantCycle(recipe, one, two, slotOnePresent, slotTwoPresent);
                        }
                    }, FMLPlugin.getMainMenuConfig().merchantDelay);
                break;
            }
        }
    }
}
