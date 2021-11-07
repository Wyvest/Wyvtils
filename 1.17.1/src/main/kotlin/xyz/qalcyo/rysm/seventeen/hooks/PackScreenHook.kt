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

package xyz.qalcyo.rysm.seventeen.hooks

import gg.essential.universal.wrappers.message.UTextComponent
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.pack.PackListWidget
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text
import xyz.qalcyo.rysm.seventeen.mixin.gui.PackAccessor
import java.util.stream.Stream

var textField: TextFieldWidget? = null
var hasChanged = false
private var changedValue = ""

fun setupTextField() {
    textField = TextFieldWidget(MinecraftClient.getInstance().textRenderer, MinecraftClient.getInstance().window.x * 4 / 5 - 1, MinecraftClient.getInstance().window.y - 13, MinecraftClient.getInstance().window.x / 5, 12, Text.of(""))
    textField!!.setMaxLength(100)
    textField!!.setChangedListener {
        hasChanged = true
        changedValue = it
    }
    textField!!.visible = true
    textField!!.setDrawsBackground(true)
}

fun filter(stream: Stream<ResourcePackOrganizer.Pack?>): Stream<ResourcePackOrganizer.Pack?> {
    if (changedValue == "") return stream
    return stream.filter {
        it != null && UTextComponent.stripFormatting((it as PackAccessor).invokeGetDisplayName().asString()).contains(changedValue.lowercase())
    }
}