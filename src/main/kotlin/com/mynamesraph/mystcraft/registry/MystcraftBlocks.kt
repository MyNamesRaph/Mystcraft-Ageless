package com.mynamesraph.mystcraft.registry

import com.mynamesraph.mystcraft.Mystcraft.Companion.MOD_ID
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object MystcraftBlocks {
    private val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(
        MOD_ID
    )

    //val EXAMPLE_BLOCK: DeferredBlock<Block> = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
    //val EXAMPLE_BLOCK_ITEM: DeferredItem<BlockItem> = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK)


    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
    }
}