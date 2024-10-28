package com.mynamesraph.mystcraft.block.writing

import com.mojang.serialization.MapCodec
import com.mynamesraph.mystcraft.registry.MystcraftBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.EnumProperty

class WritingDeskBlock(properties: Properties) : HorizontalDirectionalBlock(properties) {

    companion object {
        val FACING = BlockStateProperties.HORIZONTAL_FACING
        val SIDE = EnumProperty.create("side",WritingDeskSide::class.java)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING, SIDE)
    }

    override fun codec(): MapCodec<out HorizontalDirectionalBlock> {
        TODO("Not yet implemented")
    }


    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val direction = context.horizontalDirection.counterClockWise
        val pos = context.clickedPos
        val posOther = pos.relative(direction)
        val level = context.level
        return if (level.getBlockState(posOther).canBeReplaced(context) && level.worldBorder.isWithinBounds(posOther))
            defaultBlockState().setValue(FACING, direction).setValue(SIDE,WritingDeskSide.RIGHT)
        else null
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, placer, stack)
        if (!level.isClientSide) {
            val blockPos = pos.relative(state.getValue(FACING))
            level.setBlock(blockPos, state.setValue(FACING,state.getValue(FACING).opposite).setValue(SIDE,WritingDeskSide.LEFT), 3)
            level.blockUpdated(pos, Blocks.AIR)
            state.updateNeighbourShapes(level, pos, 3)
        }
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        if (!level.isClientSide && player.isCreative) {
            val side = state.getValue(FACING)

            if (level.getBlockState(pos.relative(side)).`is`(MystcraftBlocks.WRITING_DESK)) {
                level.destroyBlock(pos.relative(side),false)
            }
        }

        return super.playerWillDestroy(level, pos, state, player)
    }

}