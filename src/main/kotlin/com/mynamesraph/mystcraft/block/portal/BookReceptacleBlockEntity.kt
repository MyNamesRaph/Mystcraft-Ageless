package com.mynamesraph.mystcraft.block.portal

import com.mynamesraph.mystcraft.container.SingleStackHandler
import com.mynamesraph.mystcraft.registry.MystcraftBlockEntities
import com.mynamesraph.mystcraft.registry.MystcraftBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Block.UPDATE_ALL
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.LecternBlockEntity
import net.minecraft.world.level.block.state.BlockState

class BookReceptacleBlockEntity(
    pos: BlockPos,
    blockState: BlockState
) : BlockEntity(MystcraftBlockEntities.BOOK_RECEPTACLE_BLOCK_ENTITY.get(), pos, blockState),Container {

    private val bookHandler = SingleStackHandler()

    val validPortalBlocks: Set<Block> by lazy {
        setOf(
            MystcraftBlocks.BLUE_CRYSTAL_BLOCK.get(),
            MystcraftBlocks.BlUE_BOOK_RECEPTACLE.get(),
            MystcraftBlocks.YELLOW_CRYSTAL_BLOCK.get(),
            MystcraftBlocks.GREEN_CRYSTAL_BLOCK.get(),
            MystcraftBlocks.PINK_CRYSTAL_BLOCK.get(),
            MystcraftBlocks.RED_CRYSTAL_BLOCK.get()
        )
    }

    val book:ItemStack
        get() {
            return bookHandler.getItem()
        }

    val hasBook: Boolean
        get() {
            return !book.isEmpty
        }

    override fun clearContent() {
        bookHandler.getItem().copyAndClear()
        setChanged()
        breakPortal()
    }

    override fun getContainerSize(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return bookHandler.getItem().isEmpty
    }

    override fun getItem(slot: Int): ItemStack {
        return book
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val itemStack = bookHandler.extractItem(slot,amount,false)
        if (!itemStack.isEmpty) this.setChanged()
        breakPortal()
        return itemStack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return bookHandler.extractItem(64,false)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        bookHandler.setStackInSlot(slot,stack)
        setChanged()
        tryGeneratePortal()
    }

    fun insertBook(stack: ItemStack):ItemStack {
        val leftover = bookHandler.insertItem(stack,false)
        setChanged()
        tryGeneratePortal()
        return leftover
    }

    fun removeBook():ItemStack {
        val book = bookHandler.extractItem(1,false)
        setChanged()
        breakPortal()
        return book
    }

    private fun tryGeneratePortal() {
        val level = level ?: return

        if (!level.isClientSide) {
            val cornersR = solveFrameCorners(blockPos, validPortalBlocks,level,128)

            if (cornersR.isSuccess) {
                val corners = cornersR.getOrThrow()

                /*for (corner in corners.withIndex()) {
                    println("Corner ${corner.index} is ${level.getBlockState(corner.value)}")
                }*/

                val color = DyeColor.entries.random()

                val points = findAllPointsInsidePolygon(corners)
                for (point in points) {
                    if (level.getBlockState(point).isEmpty) {
                        level.setBlock(
                            point,
                            MystcraftBlocks.LINK_PORTAL.get().defaultBlockState()
                                .setValue(LinkPortalBlock.PERSISTENT, false)
                                .setValue(LinkPortalBlock.COLOR,color),
                            UPDATE_ALL
                        )
                        val be = level.getBlockEntity(point)
                        if (be is LinkPortalBlockEntity) {
                            be.receptaclePosition = blockPos
                            be.setChanged()
                        }
                    }
                }
            }
        }
    }

    private fun breakPortal() {
        val level = level ?: return

        if (!level.isClientSide) {
            for (direction in Direction.entries) {
                val block = level.getBlockState(blockPos.relative(direction))
                if (block.`is`(MystcraftBlocks.LINK_PORTAL)) {
                    level.destroyBlock(blockPos.relative(direction),false)
                }
            }
        }
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        if (tag.contains("Book", 10)) {
            bookHandler.setStackInSlot(
                0,
                ItemStack.parse(
                    registries,
                    tag.getCompound("Book")
                ).orElse(ItemStack.EMPTY)
            )
        } else {
            bookHandler.setStackInSlot(
                0,
                ItemStack.EMPTY
            )
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        if (this.hasBook) {
            tag.put("Book", this.book.save(registries))
        }
    }


    override fun stillValid(player: Player): Boolean {
        return true
    }


}