package com.mynamesraph.mystcraft.registry

import com.mynamesraph.mystcraft.Mystcraft.Companion.MOD_ID
import com.mynamesraph.mystcraft.component.*
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object MystcraftComponents {

    private val COMPONENTS: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(
        Registries.DATA_COMPONENT_TYPE, MOD_ID
    )

    val LOCATION = COMPONENTS.registerComponentType("location") {
        it.persistent(LocationComponent.CODEC)
            .networkSynchronized(LocationComponent.STREAM_CODEC)
    }

    val ROTATION = COMPONENTS.registerComponentType("rotation") {
        it.persistent(RotationComponent.CODEC)
            .networkSynchronized(RotationComponent.STREAM_CODEC)
    }

    val LOCATION_DISPLAY = COMPONENTS.registerComponentType("location_display") {
        it.persistent(LocationDisplayComponent.CODEC)
            .networkSynchronized(LocationDisplayComponent.STREAM_CODEC)
    }

    val DIMENSION_ID = COMPONENTS.registerComponentType("dimension_id") {
        it.persistent(DimensionIdentificatorComponent.CODEC)
            .networkSynchronized(DimensionIdentificatorComponent.STREAM_CODEC)
    }

    val BIOME_SYMBOLS = COMPONENTS.registerComponentType("biome_symbols") {
        it.persistent(BiomeSymbolsComponent.CODEC)
            .networkSynchronized(BiomeSymbolsComponent.STREAM_CODEC)
    }

    val IS_CREATIVE_SPAWNED = COMPONENTS.registerComponentType("is_creative_spawned") {
        it.persistent(IsCreativeSpawnedComponent.CODEC)
            .networkSynchronized(IsCreativeSpawnedComponent.STREAM_CODEC)
    }

    fun register(eventBus: IEventBus) {
        COMPONENTS.register(eventBus)
    }
}