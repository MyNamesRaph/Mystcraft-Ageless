package com.mynamesraph.mystcraft.block.portal

import com.mynamesraph.mystcraft.registry.MystcraftBlocks
import com.mynamesraph.mystcraft.registry.MystcraftTags
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult

class BookReceptacleBlock(properties: Properties): Block(properties),EntityBlock {
    companion object {
        val FACING: DirectionProperty = DirectionalBlock.FACING
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return this.defaultBlockState()
            .setValue(FACING, context.nearestLookingDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {

        if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty) {
            val be = level.getBlockEntity(pos)

            if (be is BookReceptacleBlockEntity && be.hasBook) {
                val itemStack = be.removeBook()
                player.setItemInHand(InteractionHand.MAIN_HAND,itemStack)
            }
        }

        return super.useWithoutItem(state, level, pos, player, hitResult)
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        val be = level.getBlockEntity(pos)
        if (be is BookReceptacleBlockEntity && be.hasBook) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION

        if (stack.`is`(MystcraftTags.LINKING_BOOK_TAG)) {
            return if (tryPlaceBook(player,level,stack,be))
                ItemInteractionResult.sidedSuccess(level.isClientSide)
            else
                ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
        }

        return if (stack.isEmpty && hand == InteractionHand.MAIN_HAND)
            ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
        else
            ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    private fun tryPlaceBook(
        entity:LivingEntity,
        level: Level,
        stack: ItemStack,
        be: BlockEntity?
    ): Boolean {
        if (be is BookReceptacleBlockEntity) {
            if (!be.hasBook) {
                if (!level.isClientSide) {
                    val leftover = be.insertBook(stack)
                    if (leftover != stack) {
                        entity.setItemInHand(InteractionHand.MAIN_HAND,leftover)
                        return true
                    }
                    else {
                        return false
                    }
                }
                return true
            }
            else {
                return false
            }
        }
            return false
    }


    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (!state.`is`(newState.block)) {
            this.popBook(state,level,pos)
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    private fun popBook(state:BlockState,level: Level,pos: BlockPos) {
        val be = level.getBlockEntity(pos)
        if (be is BookReceptacleBlockEntity) {
            val direction = state.getValue(FACING)
            val itemStack: ItemStack = be.book.copy()
            val f = 0.25f * direction.stepX.toFloat()
            val f1 = 0.25f * direction.stepZ.toFloat()
            val itemEntity = ItemEntity(
                level,
                pos.x.toDouble() + 0.5 + f.toDouble(),
                (pos.y + 1).toDouble(),
                pos.z.toDouble() + 0.5 + f1.toDouble(),
                itemStack
            )
            itemEntity.setDefaultPickUpDelay()
            level.addFreshEntity(itemEntity)
            be.clearContent()
        }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BookReceptacleBlockEntity(pos,state)
    }
}