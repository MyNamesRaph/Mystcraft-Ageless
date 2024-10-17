package com.mynamesraph.mystcraft.ui.screen

import com.mynamesraph.mystcraft.networking.packet.LinkingBookTravelPacket
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.neoforged.neoforge.network.PacketDistributor

open class LinkingBookScreen(title: Component, private val hand:InteractionHand) : Screen(title) {

    private val TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath("mystcraft_ageless","textures/gui/book/linking_book.png")

    private var BACKGROUND_Y:Int = 0
    private var BACKGROUND_X:Int = 0

    private var BUTTON_X = 0
    private var BUTTON_Y = 0


    override fun init() {
        super.init()

        BACKGROUND_X = (Minecraft.getInstance().screen!!.width /2) - 128
        BACKGROUND_Y = (Minecraft.getInstance().screen!!.height /2) - 103

        BUTTON_X = BACKGROUND_X + 146
        BUTTON_Y = BACKGROUND_Y + 32


        addWidget(
            Button.builder(
                Component.translatableWithFallback("mystcraft.linking_book_travel.narration","Travel")
            )
            {
                PacketDistributor.sendToServer(LinkingBookTravelPacket(hand))
                onClose()
            }
                .pos(BUTTON_X,BUTTON_Y)
                .size(80,48)
                .build()
        )
    }

    override fun tick() {
        super.tick()
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(graphics,mouseX,mouseY,partialTick)



        super.render(graphics, mouseX, mouseY, partialTick)
    }

    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        guiGraphics.blit(TEXTURE,BACKGROUND_X,BACKGROUND_Y,0,0,256,181)
    }

    override fun onClose() {
        // Stop any handlers here

        super.onClose()
    }

    override fun removed() {
        // Reset initial states here

        super.removed()
    }


}