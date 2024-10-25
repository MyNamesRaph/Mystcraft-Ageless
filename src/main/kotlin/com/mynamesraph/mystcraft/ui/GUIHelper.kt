package com.mynamesraph.mystcraft.ui

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

object GUIHelper {
    fun drawCenteredStringNoDropShadow(guiGraphics: GuiGraphics, font: Font, text: Component, x: Int, y: Int, color: Int) {
        guiGraphics.drawString(font, text, x - font.width(text) / 2, y, color,false)
    }
}