package com.mynamesraph.mystcraft.ui.screen

import com.mynamesraph.mystcraft.component.LocationComponent
import com.mynamesraph.mystcraft.component.LocationDisplayComponent
import com.mynamesraph.mystcraft.data.networking.packet.LinkingBookTravelPacket
import com.mynamesraph.mystcraft.registry.MystcraftComponents
import com.mynamesraph.mystcraft.ui.drawCenteredStringNoDropShadow
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

open class LinkingBookScreen(title: Component, private val hand:InteractionHand, private val player: Player) : Screen(title) {

    private val TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath("mystcraft_ageless","textures/gui/book/linking_book.png")
    private val TEXTURE_UNUSABLE: ResourceLocation = ResourceLocation.fromNamespaceAndPath("mystcraft_ageless","textures/gui/book/linking_book_unusable.png")

    private var BACKGROUND_Y: Int = 0
    private var BACKGROUND_X: Int = 0

    private var TITLE_X: Int = 0
    private var TITLE_Y: Int = 0

    private var BUTTON_X = 0
    private var BUTTON_Y = 0


    override fun init() {
        super.init()

        BACKGROUND_X = (Minecraft.getInstance().screen!!.width /2) - 128
        BACKGROUND_Y = (Minecraft.getInstance().screen!!.height /2) - 103

        TITLE_X = BACKGROUND_X + 70
        TITLE_Y = BACKGROUND_Y + 20

        BUTTON_X = BACKGROUND_X + 146
        BUTTON_Y = BACKGROUND_Y + 32


        val locationComponent = player.getItemInHand(hand).components.get(MystcraftComponents.LOCATION.get())

        if (locationComponent is LocationComponent) {
            if (player.level().dimension() == locationComponent.levelKey) {
                addWidget(
                    Button.builder(
                        Component.translatableWithFallback("narration.mystcraft_ageless.linking_book_travel","Travel")
                    )
                    {
                        player.displayClientMessage(
                            Component.translatable("message.mystcraft_ageless.no_same_dimension_travel"),
                            false
                        )
                        onClose()
                    }
                        .pos(BUTTON_X,BUTTON_Y)
                        .size(80,48)
                        .build()
                )            }
            else {
                addWidget(
                    Button.builder(
                        Component.translatableWithFallback("narration.mystcraft_ageless.linking_book_travel","Travel")
                    )
                    {
                        PacketDistributor.sendToServer(LinkingBookTravelPacket(hand))
                        onClose()
                    }
                        .pos(BUTTON_X,BUTTON_Y)
                        .size(80,48)
                        .build()
                )            }


        }


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

        val locationComponent = player.getItemInHand(hand).components.get(MystcraftComponents.LOCATION.get())

        if (locationComponent is LocationComponent) {
            if (player.level().dimension() == locationComponent.levelKey) {
                guiGraphics.blit(TEXTURE_UNUSABLE,BACKGROUND_X,BACKGROUND_Y,0,0,256,181)
            }
            else {
                guiGraphics.blit(TEXTURE,BACKGROUND_X,BACKGROUND_Y,0,0,256,181)
            }
        }


        val titleComponent = player.getItemInHand(hand).components.get(MystcraftComponents.LOCATION_DISPLAY.get())
        if (titleComponent is LocationDisplayComponent) {
            var unformattedTitle = titleComponent.name.copy()
            unformattedTitle = unformattedTitle.withStyle(Style.EMPTY.withColor(0x303030).withUnderlined(true).withBold(true))

            drawCenteredStringNoDropShadow(guiGraphics,this.font, unformattedTitle, TITLE_X, TITLE_Y, 0)
        }
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