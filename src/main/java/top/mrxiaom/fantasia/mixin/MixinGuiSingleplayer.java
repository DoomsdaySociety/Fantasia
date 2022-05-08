package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.gui.GuiWorldSelection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.mrxiaom.fantasia.ModWrapper;

@Mixin(GuiWorldSelection.class)
public class MixinGuiSingleplayer {
    @Inject(at = @At("HEAD"), method = "drawScreen(IIF)V")
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_, CallbackInfo ci) {
        ModWrapper.drawBackground();
    }
}
