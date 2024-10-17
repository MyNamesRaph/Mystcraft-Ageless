package com.mynamesraph.mystcraft.ui.screen

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack


class LecternMenu @JvmOverloads constructor(
    containerId: Int,
    lectern: Container = SimpleContainer(1),
    lecternData: ContainerData = SimpleContainerData(1)
) :
    AbstractContainerMenu(MenuType.LECTERN, containerId) {
    private val lectern: Container
    private val lecternData: ContainerData

    init {
        checkContainerSize(lectern, 1)
        checkContainerDataCount(lecternData, 1)
        this.lectern = lectern
        this.lecternData = lecternData
        this.addSlot(object : Slot(lectern, 0, 0, 0) {
            override fun setChanged() {
                super.setChanged()
                this@LecternMenu.slotsChanged(this.container)
            }
        })
        this.addDataSlots(lecternData)
    }

    /**
     * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
     */
    override fun clickMenuButton(player: Player, id: Int): Boolean {
        if (id >= 100) {
            val k = id - 100
            this.setData(0, k)
            return true
        } else {
            when (id) {
                1 -> {
                    val j = lecternData[0]
                    this.setData(0, j - 1)
                    return true
                }

                2 -> {
                    val i = lecternData[0]
                    this.setData(0, i + 1)
                    return true
                }

                3 -> {
                    if (!player.mayBuild()) {
                        return false
                    }

                    val itemstack = lectern.removeItemNoUpdate(0)
                    lectern.setChanged()
                    if (!player.inventory.add(itemstack)) {
                        player.drop(itemstack, false)
                    }

                    return true
                }

                else -> return false
            }
        }
    }

    /**
     * Handle when the stack in slot `index` is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
     */
    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun setData(id: Int, data: Int) {
        super.setData(id, data)
        this.broadcastChanges()
    }

    /**
     * Determines whether supplied player can use this container
     */
    override fun stillValid(player: Player): Boolean {
        return lectern.stillValid(player)
    }

    val book: ItemStack
        get() = lectern.getItem(0)

    val page: Int
        get() = lecternData[0]

    companion object {
        private const val DATA_COUNT = 1
        private const val SLOT_COUNT = 1
        const val BUTTON_PREV_PAGE: Int = 1
        const val BUTTON_NEXT_PAGE: Int = 2
        const val BUTTON_TAKE_BOOK: Int = 3
        const val BUTTON_PAGE_JUMP_RANGE_START: Int = 100
    }
}