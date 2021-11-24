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

package xyz.qalcyo.rysm.eight.mixin.gui;

import net.minecraft.client.resources.ResourcePackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.qalcyo.rysm.eight.hooks.GuiScreenResourcePacksHookKt;

import java.io.FileFilter;

@Mixin(ResourcePackRepository.class)
public class ResourcePackRepositoryMixin {
    @ModifyArg(method = "getResourcePackFiles", at = @At(value = "INVOKE", target = "Ljava/io/File;listFiles(Ljava/io/FileFilter;)[Ljava/io/File;"), index = 0)
    private FileFilter modifyFilter(FileFilter original) {
        return GuiScreenResourcePacksHookKt.getResourcePackFilter();
    }
}
