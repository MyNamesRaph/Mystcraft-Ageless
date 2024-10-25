package com.mynamesraph.mystcraft.registry

import com.mynamesraph.mystcraft.Mystcraft
import com.mynamesraph.mystcraft.block.portal.BookReceptacleBlockEntity
import com.mynamesraph.mystcraft.block.portal.LinkPortalBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object MystcraftBlockEntities {
    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(
        Registries.BLOCK_ENTITY_TYPE,
        Mystcraft.MOD_ID
    )

    val LINK_PORTAL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
        "link_portal",
        Supplier {
            BlockEntityType.Builder.of(
                ::LinkPortalBlockEntity,
                MystcraftBlocks.LINK_PORTAL.get()
            ).build(null)
        }
    )

    val BOOK_RECEPTACLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
        "book_receptacle",
        Supplier {
            BlockEntityType.Builder.of(
                ::BookReceptacleBlockEntity,
                MystcraftBlocks.BlUE_BOOK_RECEPTACLE.get(),
                //TODO: ADD OTHER RECEPTACLES
            ).build(null)
        }
    )

    fun register(eventBus: IEventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus)
    }
}