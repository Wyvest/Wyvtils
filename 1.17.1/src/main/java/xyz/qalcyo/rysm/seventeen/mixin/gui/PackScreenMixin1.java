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

package xyz.qalcyo.rysm.seventeen.mixin.gui;

import net.minecraft.client.gui.screen.pack.PackScreen;
import org.spongepowered.asm.mixin.Mixin;
import xyz.qalcyo.rysm.seventeen.hooks.PackScreenHookKt;

import java.util.Objects;

@Mixin(PackScreen.class)
public class PackScreenMixin1 {
    public boolean method_25404(int int_1, int int_2, int int_3) {
        return Objects.requireNonNull(PackScreenHookKt.getTextField()).keyPressed(int_1, int_2, int_3);
    }

    public boolean method_25400(char char_1, int int_1) {
        return Objects.requireNonNull(PackScreenHookKt.getTextField()).charTyped(char_1, int_1);
    }
}
