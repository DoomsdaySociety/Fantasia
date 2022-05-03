package top.mrxiaom.fantasia.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import top.mrxiaom.fantasia.ModWrapper;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private boolean fullscreen;
    @Final
    @Shadow
    private Session session;

    @Shadow
    abstract void updateDisplayMode() throws LWJGLException;
    /**
     * @author MrXiaoM
     * @reason 修改标题
     */
    @Overwrite
    private void createDisplay() throws LWJGLException
    {
        Display.setResizable(true);
        ModWrapper.updateTitle();

        try
        {
            Display.create((new PixelFormat()).withDepthBits(24));
        }
        catch (LWJGLException lwjglexception)
        {
            LOGGER.error("Couldn't set pixel format", lwjglexception);

            try
            {
                Thread.sleep(1000L);
            }
            catch (InterruptedException ignored)
            {
            }

            if (this.fullscreen)
            {
                this.updateDisplayMode();
            }
            Display.create();
        }
    }
}
