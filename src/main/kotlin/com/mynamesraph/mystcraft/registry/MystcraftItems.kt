package com.mynamesraph.mystcraft.registry

import com.mynamesraph.mystcraft.Mystcraft.Companion.MOD_ID
import com.mynamesraph.mystcraft.component.LocationComponent
import com.mynamesraph.mystcraft.component.RotationComponent
import com.mynamesraph.mystcraft.item.LinkingBookItem
import com.mynamesraph.mystcraft.registry.MystcraftComponents.LOCATION_COMPONENT
import com.mynamesraph.mystcraft.registry.MystcraftComponents.ROTATION_COMPONENT
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.ItemLore
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.joml.Vector3f

object MystcraftItems {
    private val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(
        MOD_ID
    )

    val LINKING_BOOK: DeferredItem<Item> = ITEMS.register("linking_book", fun(): LinkingBookItem {
        return LinkingBookItem(
            Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .component(
                    DataComponents.LORE, ItemLore(
                        listOf(
                            Component.literal("No Location")
                                .withStyle(Style.EMPTY.withItalic(false).withColor(0xAAAAAA))
                        )
                    )
                )
                .component(
                    LOCATION_COMPONENT, LocationComponent(ServerLevel.OVERWORLD, Vector3f(0.0f, -60.0f, 0.0f))
                )
                .component(
                    ROTATION_COMPONENT, RotationComponent(0.0f,0.0f)
                )
        )
    })



    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
    }
}