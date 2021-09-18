package xyz.qalcyo.qaltils.gui

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ClientBossBar
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.boss.BossBar
import net.minecraft.text.StringVisitable
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import xyz.qalcyo.qaltils.config.QaltilsConfig
import xyz.qalcyo.qaltils.config.QaltilsConfig.bossBarBar
import xyz.qalcyo.qaltils.config.QaltilsConfig.bossBarShadow
import xyz.qalcyo.qaltils.config.QaltilsConfig.bossBarText
import xyz.qalcyo.qaltils.config.QaltilsConfig.bossBarX
import xyz.qalcyo.qaltils.config.QaltilsConfig.bossBarY
import xyz.qalcyo.qaltils.config.QaltilsConfig.bossbarScale
import java.util.*

class BossBarGui(private var parent: Screen?) : Screen(Text.of("Qaltils")) {

    private val bossBar = ClientBossBar(
        UUID.fromString("cd899a14-de78-4de8-8d31-9d42fff31d7a"),
        Text.of("Qaltils"),
        1.0F,
        BossBar.Color.PURPLE,
        BossBar.Style.NOTCHED_20,
        false,
        false,
        false
    ) //cd899a14-de78-4de8-8d31-9d42fff31d7a is the UUID of EssentialBot which should never appear ingame
    private var prevX = 0
    private var prevY = 0
    private var bossBarDragging = false
    private val barsTexture = Identifier("textures/gui/bars.png")

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        updatePos(mouseX, mouseY)
        super.render(matrices, mouseX, mouseY, delta)
        client!!.profiler.push("bossBarGui")
        matrices.push()
        val iHaveNoIdeaWhatToNameThisFloat = bossbarScale.toDouble() - 1.0f
        matrices.translate(
            -bossBarX * iHaveNoIdeaWhatToNameThisFloat / 2,
            -bossBarY * iHaveNoIdeaWhatToNameThisFloat,
            0.0
        )
        matrices.scale(bossbarScale, bossbarScale, 1f)
        val i = bossBarX
        val j = bossBarY

        val k = i / 2 - 91
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, barsTexture)

        if (bossBarBar) {
            renderBossBar(matrices, k, j, bossBar)
        }

        if (bossBarText) {
            val text = bossBar.name
            val m = client!!.textRenderer.getWidth(text as StringVisitable)
            val n = i / 2 - m / 2
            val o = j - 9
            if (bossBarShadow) {
                client!!.textRenderer.drawWithShadow(matrices, text, n.toFloat(), o.toFloat(), 16777215)
            } else {
                client!!.textRenderer.draw(matrices, text, n.toFloat(), o.toFloat(), 16777215)
            }
        }
        client!!.profiler.pop()
        matrices.pop()
    }

    private fun renderBossBar(matrices: MatrixStack, x: Int, y: Int, bossBar: BossBar) {
        this.drawTexture(matrices, x, y, 0, bossBar.color.ordinal * 5 * 2, 182, 5)
        if (bossBar.style != BossBar.Style.PROGRESS) {
            this.drawTexture(matrices, x, y, 0, 80 + (bossBar.style.ordinal - 1) * 5 * 2, 182, 5)
        }
        val i = (bossBar.percent * 183.0f).toInt()
        if (i > 0) {
            this.drawTexture(matrices, x, y, 0, bossBar.color.ordinal * 5 * 2 + 5, i, 5)
            if (bossBar.style != BossBar.Style.PROGRESS) {
                this.drawTexture(matrices, x, y, 0, 80 + (bossBar.style.ordinal - 1) * 5 * 2 + 5, i, 5)
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        prevX = mouseX.toInt()
        prevY = mouseY.toInt()
        if (mouseButton == 0) {
            bossBarDragging = true
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            InputUtil.GLFW_KEY_UP -> bossBarY -= 5
            InputUtil.GLFW_KEY_DOWN -> bossBarY += 5
            InputUtil.GLFW_KEY_LEFT -> bossBarX -= 5
            InputUtil.GLFW_KEY_RIGHT -> bossBarX += 5
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    private fun updatePos(x: Int, y: Int) {
        if (bossBarDragging) {
            bossBarX = prevX
            bossBarY = prevY
        }
        prevX = x
        prevY = y
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, state: Int): Boolean {
        bossBarDragging = false
        return super.mouseReleased(mouseX, mouseY, state)
    }


    override fun onClose() {
        QaltilsConfig.markAndWrite()
        MinecraftClient.getInstance().setScreen(parent)
        parent = null
    }
}