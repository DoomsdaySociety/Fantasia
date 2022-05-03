package top.mrxiaom.fantasia.mixin;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Scoreboard.class)
public class FixScoreboard {
    @Inject(at = @At("HEAD"), method = "removeTeam*", cancellable = true)
    public void removeTeam(ScorePlayerTeam playerTeam, CallbackInfo ci) {
        if (playerTeam == null) ci.cancel();
    }
}
