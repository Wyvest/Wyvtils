package xyz.qalcyo.qaltils.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import xyz.qalcyo.qaltils.config.QaltilsConfig;
import xyz.qalcyo.qaltils.gui.SidebarGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    private int scaledHeight;

    private int sidebarWidth;
    private int sidebarY;
    private int sidebarX;

    @Shadow
    protected abstract void drawTextBackground(MatrixStack matrices, TextRenderer textRenderer, int yOffset, int width, int color);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"))
    private void removeTranslation(MatrixStack matrixStack, double x, double y, double z) {
        if (y == (double) (scaledHeight - 68)) {
            if ((!QaltilsConfig.INSTANCE.getActionBarPosition() && QaltilsConfig.INSTANCE.getActionBarCustomization()) || !QaltilsConfig.INSTANCE.getActionBarCustomization()) {
                matrixStack.translate(x, y, z);
            }
        } else {
            matrixStack.translate(x, y, z);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTextBackground(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;III)V"))
    private void modifyActionBarBackground(InGameHud inGameHud, MatrixStack matrices, TextRenderer textRenderer, int yOffset, int width, int color) {
        if (yOffset == -4) {
            if (QaltilsConfig.INSTANCE.getActionBarCustomization() && QaltilsConfig.INSTANCE.getActionBar()) {
                drawTextBackground(matrices, textRenderer, yOffset, width, color);
            }
        } else {
            drawTextBackground(matrices, textRenderer, yOffset, width, color);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int modifyActionBar(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (QaltilsConfig.INSTANCE.getActionBarCustomization()) {
            if (QaltilsConfig.INSTANCE.getActionBar()) {
                if (QaltilsConfig.INSTANCE.getActionBarShadow()) {
                    return textRenderer.drawWithShadow(matrices, text, (QaltilsConfig.INSTANCE.getActionBarPosition() ? QaltilsConfig.INSTANCE.getActionBarX() : x), (QaltilsConfig.INSTANCE.getActionBarPosition() ? QaltilsConfig.INSTANCE.getActionBarY() : y), color);
                } else {
                    return textRenderer.draw(matrices, text, (QaltilsConfig.INSTANCE.getActionBarPosition() ? QaltilsConfig.INSTANCE.getActionBarX() : x), (QaltilsConfig.INSTANCE.getActionBarPosition() ? QaltilsConfig.INSTANCE.getActionBarY() : y), color);
                }
            }
        } else {
            return textRenderer.draw(matrices, text, x, y, color);
        }
        return 0;
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"))
    private void modifyTitleTranslate(Args args) {
        if (((float) args.get(0)) == 4.0F) {
            args.set(0, 4.0F * QaltilsConfig.INSTANCE.getTitleScale());
            args.set(1, 4.0F * QaltilsConfig.INSTANCE.getTitleScale());
        } else if (((float) args.get(0)) == 2.0F) {
            args.set(0, 2.0F * QaltilsConfig.INSTANCE.getSubtitleScale());
            args.set(1, 2.0F * QaltilsConfig.INSTANCE.getSubtitleScale());
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void cancelScoreboard(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        if (!QaltilsConfig.INSTANCE.getSidebar()) {
            ci.cancel();
        } else if (MinecraftClient.getInstance().currentScreen instanceof SidebarGui) {
            ci.cancel();
        } else {
            matrices.push();
        }
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I"))
    private int getMax(int a, int b) {
        sidebarWidth = Math.max(a, b);
        return sidebarWidth;
    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At("STORE"), index = 12)
    private int modifyY(int m) {
        sidebarY = (QaltilsConfig.INSTANCE.getSidebarPosition() ? QaltilsConfig.INSTANCE.getSidebarY() : m);
        return sidebarY;
    }


    @ModifyVariable(method = "renderScoreboardSidebar", at = @At("STORE"), index = 14)
    private int modifyX(int m, MatrixStack matrices, ScoreboardObjective objective) {
        if (QaltilsConfig.INSTANCE.getSidebarPosition()) {
            return QaltilsConfig.INSTANCE.getSidebarX() - sidebarWidth - 3;
        } else {
            return m;
        }

    }

    @ModifyVariable(method = "renderScoreboardSidebar", at = @At("STORE"), index = 25)
    private int modifyX2(int m) {
        sidebarX = (QaltilsConfig.INSTANCE.getSidebarPosition() ? QaltilsConfig.INSTANCE.getSidebarX() - 1 : m);
        return sidebarX;
    }

    @Inject(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;getSecond()Ljava/lang/Object;"))
    private void scale(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        float iHaveNoIdeaWhatToNameThisFloat = QaltilsConfig.INSTANCE.getSidebarScale() - 1.0f;
        matrices.translate(-sidebarX * iHaveNoIdeaWhatToNameThisFloat, -sidebarY * iHaveNoIdeaWhatToNameThisFloat, 0.0f);
        matrices.scale(QaltilsConfig.INSTANCE.getSidebarScale(), QaltilsConfig.INSTANCE.getSidebarScale(), 1.0F);
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int addTextShadow(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {
        if (QaltilsConfig.INSTANCE.getSidebarTextShadow()) {
            return textRenderer.drawWithShadow(matrices, text, x, y, color);
        } else {
            return textRenderer.draw(matrices, text, x, y, color);
        }
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"))
    private void removeBackground(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        if (QaltilsConfig.INSTANCE.getSidebarBackground()) {
            DrawableHelper.fill(matrices, x1, y1, sidebarX, y2, QaltilsConfig.INSTANCE.getSidebarBackgroundColor().getRGB());
        }
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int modifyRedText(TextRenderer textRenderer, MatrixStack matrices, String text, float x, float y, int color) {
        if (QaltilsConfig.INSTANCE.getSidebarScorePoints()) {
            if (QaltilsConfig.INSTANCE.getSidebarTextShadow()) {
                return textRenderer.drawWithShadow(matrices, text, x, y, color);
            } else {
                return textRenderer.draw(matrices, text, x, y, color);
            }
        }
        return 0;
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("TAIL"))
    private void pop(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        matrices.pop();
    }

}