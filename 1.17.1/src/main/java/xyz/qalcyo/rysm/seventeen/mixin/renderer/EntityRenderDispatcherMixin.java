/*
 * Rysm, a utility mod for 1.8.9.
 * Copyright (C) 2021 Rysm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.qalcyo.rysm.seventeen.mixin.renderer;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import xyz.qalcyo.rysm.core.config.RysmConfig;
import xyz.qalcyo.rysm.seventeen.hooks.EntityRenderDispatcherHookKt;

/**
 * This mixin sends and handles the HitboxRenderEvent
 * which is used to handle hitbox related features in
 * Rysm. This also manually adds some Rysm features related
 * to the hitbox.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    private boolean renderHitboxes;

    /**
     * Invokes a HitboxRenderEvent and cancels the rendering of the hitbox accordingly.
     */
    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void invokeHitboxEvent(MatrixStack matrices, VertexConsumer vertices, Entity entityIn, float tickDelta, CallbackInfo ci) {
        EntityRenderDispatcherHookKt.invokeHitboxEvent(entityIn, ci);
    }

    /**
     * Cancels and changes the colour and size of the hitbox accordingly.
     */
    @Redirect(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/math/Box;FFFF)V"))
    private static void modifyBox(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, float red, float green, float blue, float alpha) {
        EntityRenderDispatcherHookKt.modifyBox(matrices, vertexConsumer, box);
    }

    /**
     * Cancels and changes the colour and size of the hitbox line of sight accordingly.
     */
    @Redirect(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawBox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;DDDDDDFFFF)V"))
    private static void modifyLineOfSight(MatrixStack matrices, VertexConsumer vertexConsumer, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        EntityRenderDispatcherHookKt.modifyLineOfSight(matrices, vertexConsumer, x1, y1, z1, x2, y2, z2);
    }

    /**
     * Cancels the hitbox eye line accordingly.
     */
    @Inject(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;vertex(Lnet/minecraft/util/math/Matrix4f;FFF)Lnet/minecraft/client/render/VertexConsumer;"), cancellable = true)
    private static void cancelEyeline(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, CallbackInfo ci) {
        EntityRenderDispatcherHookKt.cancelEyeline(ci);
    }

    /**
     * Changes the colour of the hitbox eye line accordingly.
     */
    @ModifyArgs(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;color(IIII)Lnet/minecraft/client/render/VertexConsumer;"))
    private static void modifyEyeLine(Args args) {
        EntityRenderDispatcherHookKt.modifyEyeLine(args);
    }

    @Shadow
    public abstract void setRenderHitboxes(boolean value);

    /**
     * Forces the hitbox to render.
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void forceHitboxes(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!renderHitboxes && RysmConfig.INSTANCE.getForceHitbox()) {
            setRenderHitboxes(true);
        }
    }
}
