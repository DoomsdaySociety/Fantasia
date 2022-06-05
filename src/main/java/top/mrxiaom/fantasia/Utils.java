package top.mrxiaom.fantasia;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String connect(List<String> array, String s) {
        String result = "";
        for (int i = 0; i < array.size(); i++) {
            result += array.get(i);
            if (i < array.size() - 1) result += s;
        }
        return result;
    }

    public static String connect(String[] array, String s) {
        String result = "";
        for (int i = 0; i < array.length; i++) {
            result += array[i];
            if (i < array.length - 1) result += s;
        }
        return result;
    }

    public static String removeColors(String s) {
        Matcher m = Pattern.compile("§.").matcher(s);
        while (m.find()) {
            s = s.replace(m.group(), "");
        }
        return s;
    }

    public static String readAsString(File file) {
        StringBuilder result = new StringBuilder();
        try (
                FileInputStream input = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(reader)) {
            String lineTxt;
            boolean a = false;
            // 逐行读取
            while ((lineTxt = br.readLine()) != null) {
                // 输出内容到控制台
                if (a) result.append("\n");
                else a = true;
                result.append(lineTxt);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void saveFromString(File file, String content) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Throwable ignored) {
        }
        try (
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            BufferedWriter out = new BufferedWriter(osw);
            out.write(content);
            out.flush();
            out.close();

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor,
                                        float zLevel) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buf.pos((double) right, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
        buf.pos((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
        buf.pos((double) left, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
        buf.pos((double) right, (double) bottom, (double) zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawRect(float left, float top, float right, float bottom, float thickness, float alpha, float red, float green, float blue) {
        fillRect(left - thickness, top, left, bottom, alpha, red, green, blue);
        fillRect(right, top, right + thickness, bottom, alpha, red, green, blue);
        fillRect(left - thickness, top - thickness, right + thickness, top, alpha, red, green, blue);
        fillRect(left - thickness, bottom, right + thickness, bottom + thickness, alpha, red, green, blue);
    }

    public static void fillRect(float left, float top, float right, float bottom, float alpha, float red, float green, float blue) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }
        alpha /= 255.0f;
        red /= 255.0f;
        green /= 255.0f;
        blue /= 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, bottom, 0.0D).endVertex();
        bufferbuilder.pos(right, top, 0.0D).endVertex();
        bufferbuilder.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
