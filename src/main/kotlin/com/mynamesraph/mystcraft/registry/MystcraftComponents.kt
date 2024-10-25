package com.mynamesraph.mystcraft.registry

import com.mynamesraph.mystcraft.Mystcraft.Companion.MOD_ID
import com.mynamesraph.mystcraft.component.LocationComponent
import com.mynamesraph.mystcraft.component.LocationDisplayComponent
import com.mynamesraph.mystcraft.component.RotationComponent
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister

object MystcraftComponents {

    private val COMPONENTS: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(
        Registries.DATA_COMPONENT_TYPE, MOD_ID
    )

    val LOCATION_COMPONENT = COMPONENTS.registerComponentType("location") {
            builder ->
        builder
            .persistent(LocationComponent.CODEC)
            .networkSynchronized(LocationComponent.STREAM_CODEC)
    }

    val ROTATION_COMPONENT = COMPONENTS.registerComponentType("rotation") {
            builder ->
        builder
            .persistent(RotationComponent.CODEC)
            .networkSynchronized(RotationComponent.STREAM_CODEC)
    }

    val LOCATION_DISPLAY_COMPONENT = COMPONENTS.registerComponentType("location_display") {
            builder ->
        builder
            .persistent(LocationDisplayComponent.CODEC)
            .networkSynchronized(LocationDisplayComponent.STREAM_CODEC)
    }

    fun register(eventBus: IEventBus) {
        COMPONENTS.register(eventBus)
    }
}