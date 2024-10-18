package com.mynamesraph.mystcraft.registry

import com.mynamesraph.mystcraft.Mystcraft.Companion.MOD_ID
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object MystcraftTabs {
    private val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(
        Registries.CREATIVE_MODE_TAB,
        MOD_ID
    )

    val MYSTCRAFT_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = CREATIVE_MODE_TABS.register("mystcraft_ageless_tab",
        Supplier {
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.mystcraft_ageless"))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon { MystcraftItems.LINKING_BOOK.get().defaultInstance }
                .displayItems { parameters: ItemDisplayParameters?, output: CreativeModeTab.Output ->
                    output.accept(MystcraftItems.LINKING_BOOK.get())
                    /*output.accept(MystcraftBlocks.BLUE_CRYSTAL_BLOCK)
                    output.accept(MystcraftBlocks.BUDDING_BLUE_CRYSTAL)*/

                    for (block in MystcraftItems.ITEMS.entries) {
                        output.accept(block.get())
                    }
                }.build()
        })

    fun register(eventBus: IEventBus) {
        CREATIVE_MODE_TABS.register(eventBus)
    }
}