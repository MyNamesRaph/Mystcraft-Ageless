package com.mynamesraph.mystcraft.ui.screen

import com.mynamesraph.mystcraft.Mystcraft
import com.mynamesraph.mystcraft.component.BiomeSymbolsComponent
import com.mynamesraph.mystcraft.component.LocationDisplayComponent
import com.mynamesraph.mystcraft.data.networking.packet.WritingDeskRenamePacket
import com.mynamesraph.mystcraft.data.networking.packet.WritingDeskSymbolPacket
import com.mynamesraph.mystcraft.registry.MystcraftComponents
import com.mynamesraph.mystcraft.ui.getDisplayCharacterForBiome
import com.mynamesraph.mystcraft.ui.menu.WritingDeskMenu
import com.mynamesraph.mystcraft.ui.widget.TextButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor

class WritingDeskScreen(
    menu: WritingDeskMenu,
    inventory: Inventory,
    title: Component
): AbstractContainerScreen<WritingDeskMenu>(menu, inventory, title) {

    private lateinit var bookName: EditBox

    private val INVENTORY_TX = ResourceLocation.fromNamespaceAndPath(
        Mystcraft.MOD_ID,
        "textures/gui/writing_desk/inventory.png"
    )
    private var INVENTORY_X = 0
    private var INVENTORY_Y = 0

    private val BOOK_NAME_TX = ResourceLocation.fromNamespaceAndPath(
        Mystcraft.MOD_ID,
        "writing_desk/text_bar"
    )

    private val BOOK_NAME_DISABLED_TX = ResourceLocation.fromNamespaceAndPath(
        Mystcraft.MOD_ID,
        "writing_desk/text_bar_disabled"
    )

    private var BOOK_NAME_X = 0
    private var BOOK_NAME_Y = 0

    private var lastTickItem = ItemStack.EMPTY
    private var lastTickNoteBook = ItemStack.EMPTY

    private val NOTEBOOK_BG_TX = ResourceLocation.withDefaultNamespace(
        "textures/gui/book.png"
    )

    private var NOTEBOOK_X = 0
    private var NOTEBOOK_Y = 0

    private var BUTTONS_X = 0
    private var BUTTONS_Y = 0
    private var BUTTON_SIZE = 13

    private var SYMBOLS_X = 0
    private var SYMBOLS_Y = 0

    private val DNI_FONT = ResourceLocation.fromNamespaceAndPath(
    Mystcraft.MOD_ID,
    "dni"
    )

    private var buttons: MutableList<TextButton> = mutableListOf()

    override fun init() {
        super.init()

        val centerX = Minecraft.getInstance().screen!!.width.div(2)
        val centerY = Minecraft.getInstance().screen!!.height.div(2)

        imageWidth = width/2
        imageHeight = height/2

        inventoryLabelX = 98
        inventoryLabelY = 80

        INVENTORY_X = centerX - 179
        INVENTORY_Y = centerY - 91

        BOOK_NAME_X = centerX + 32
        BOOK_NAME_Y = centerY - 84

        NOTEBOOK_X = INVENTORY_X + 12
        NOTEBOOK_Y = INVENTORY_Y

        BUTTONS_X = NOTEBOOK_X + 35
        BUTTONS_Y = NOTEBOOK_Y + 17

        SYMBOLS_X = BOOK_NAME_X - 25
        SYMBOLS_Y = BUTTONS_Y + 10


        bookName = EditBox(font,BOOK_NAME_X+3,BOOK_NAME_Y+4,133,12,Component.translatable("mystcraft_ageless.writing_desk.rename"))
        bookName.setCanLoseFocus(true)
        bookName.setTextColor(-1)
        bookName.setTextColorUneditable(-1)
        bookName.setMaxLength(16)
        bookName.setResponder(::onBookNameChanged)
        bookName.isBordered = false
        bookName.value = ""
        bookName.setEditable(menu.getSlot(0).hasItem())
        addWidget(bookName)

        var count = 0

        for (j in 0..9) {
            for (i in 0..7) {
                val str = "I'm button #${count++}"

                buttons.addLast(
                    addRenderableWidget(
                        TextButton(
                            BUTTONS_X + (BUTTON_SIZE+1)*i,
                            BUTTONS_Y + (BUTTON_SIZE+1)*j,
                            BUTTON_SIZE,
                            BUTTON_SIZE,
                            Component.literal(i.toString()).withStyle(Style.EMPTY.withFont(DNI_FONT).withColor(0)),
                            Tooltip.create(Component.literal(i.toString())),
                            2
                        ) {
                            println(str)
                        }
                    )
                )
            }

        }

    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBg(guiGraphics,partialTick,mouseX,mouseY)
        renderFg(guiGraphics,partialTick,mouseX,mouseY)

        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics,mouseX,mouseY)
    }

    override fun containerTick() {
        super.containerTick()
        bookName.setEditable(menu.getSlot(0).hasItem())
        bookName.isVisible = menu.getSlot(0).hasItem()

        if (menu.getSlot(0).hasItem()) {
            if (lastTickItem != menu.getSlot(0).item) {
                val item = menu.getSlot(0).item
                val display = item.components.get(MystcraftComponents.LOCATION_DISPLAY.get())

                if (display is LocationDisplayComponent) {
                    if (!bookName.value.contains(display.name.string)) { // This will break in some cases and I do not give a fuck
                        bookName.value = display.name.string
                    }
                }

                lastTickItem = menu.getSlot(0).item
            }
        }
        else {
            bookName.value = ""
            lastTickItem = ItemStack.EMPTY
        }

    }

    private fun renderFg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        bookName.render(guiGraphics,mouseX,mouseY,partialTick)

        if (menu.getSlot(1).hasItem()) {
            for (button in buttons) {
                if (lastTickNoteBook != menu.getSlot(1).item) {
                    val item = menu.getSlot(1).item
                    //TODO: adapt for different types of symbols
                    val symbols = item.components.get(MystcraftComponents.BIOME_SYMBOLS.get())

                    if (symbols is BiomeSymbolsComponent) {
                        for (buttonI in buttons.withIndex()) {
                            //TODO: add pages when there's too many
                            val biomesOrdered = symbols.biomes.sorted()

                            if (buttonI.index < symbols.biomes.count()) {
                                buttonI.value.active = true
                                buttonI.value.visible = true

                                buttonI.value.tooltip = Tooltip.create(Component.translatable("biome.${biomesOrdered[buttonI.index].toLanguageKey()}"))
                                buttonI.value.message = Component.literal(
                                    getDisplayCharacterForBiome(
                                        biomesOrdered[buttonI.index]
                                    )
                                ).withStyle(buttonI.value.message.style)

                                buttonI.value.onPress = {
                                    PacketDistributor.sendToServer(
                                        WritingDeskSymbolPacket(
                                            "BIOME",
                                            biomesOrdered[buttonI.index],
                                            menu.pos
                                        )
                                    )
                                }
                            }
                            else {
                                buttonI.value.active = false
                                buttonI.value.visible = false
                            }
                        }
                    }
                    lastTickNoteBook = item
                }
            }
        }
        else {
            for (button in buttons) {
                button.active = false
                button.visible = false
            }
            lastTickNoteBook = ItemStack.EMPTY
        }

        if (menu.getSlot(0).hasItem()) {
            val item = menu.getSlot(0).item

            if (item.has(MystcraftComponents.BIOME_SYMBOLS)) {
                val symbols = item.components.get(MystcraftComponents.BIOME_SYMBOLS.get())

                if (symbols is BiomeSymbolsComponent) {
                    for (biome in symbols.biomes.withIndex()) {
                        guiGraphics.drawString(
                            font,
                            Component.literal(getDisplayCharacterForBiome(biome.value)).withStyle(Style.EMPTY.withFont(DNI_FONT)),
                            SYMBOLS_X + biome.index * 7,
                            SYMBOLS_Y,
                            0
                        )
                    }

                }
            }

        }
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blit(INVENTORY_TX, INVENTORY_X, INVENTORY_Y,0F,0F,358,181,512,512)

        if (menu.getSlot(1).hasItem()) {
            guiGraphics.blit(NOTEBOOK_BG_TX,NOTEBOOK_X,NOTEBOOK_Y,0,0,256,256)
        }


        if (menu.getSlot(0).hasItem()) {
            guiGraphics.blitSprite(BOOK_NAME_TX,BOOK_NAME_X,BOOK_NAME_Y,140,16)
        }
        else {
            guiGraphics.blitSprite(BOOK_NAME_DISABLED_TX,BOOK_NAME_X,BOOK_NAME_Y,140,16)
        }
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        guiGraphics.drawString(
            this.font,
            this.playerInventoryTitle,
            this.inventoryLabelX,
            this.inventoryLabelY, 4210752, false
        )
    }

    private fun onBookNameChanged(name: String) {
        val slot = menu.getSlot(0)

        if (slot.hasItem()) {
            if (slot.item.has(MystcraftComponents.LOCATION_DISPLAY)) {
                PacketDistributor.sendToServer(
                    WritingDeskRenamePacket(
                        name,
                        menu.pos
                    )
                )
            }
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == 256) {
            minecraft!!.player!!.closeContainer()
        }

        return if (!this.bookName.keyPressed(keyCode, scanCode, modifiers)
            && !this.bookName.canConsumeInput()
            ) super.keyPressed(keyCode, scanCode, modifiers) else true
    }
}