package com.mynamesraph.mystcraft.item

import com.mynamesraph.mystcraft.component.BiomeSymbolsComponent
import com.mynamesraph.mystcraft.component.IsCreativeSpawnedComponent
import com.mynamesraph.mystcraft.registry.MystcraftComponents
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class NotebookItem(properties: Properties) : Item(properties) {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val creativeSpawned = stack.components.get(MystcraftComponents.IS_CREATIVE_SPAWNED.get())
        if (creativeSpawned is IsCreativeSpawnedComponent) {
            tooltipComponents.add(Component.translatable("mystcraft_ageless.tooltip_messages.creative_spawned"))
        }

        val biomes = stack.components.get(MystcraftComponents.BIOME_SYMBOLS.get())
        if (biomes is BiomeSymbolsComponent) {
            tooltipComponents.add(Component.translatable("mystcraft_ageless.tooltip_messages.biomes",biomes.biomes.count()))
        }
    }


    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (!level.isClientSide) {
            val server = entity.server!!

            if (stack.components.has(MystcraftComponents.IS_CREATIVE_SPAWNED.get())) {
                if (stack.components.get(MystcraftComponents.IS_CREATIVE_SPAWNED.get())!!.generated) return
                val patchedComponents = PatchedDataComponentMap(stack.components)

                patchedComponents.set(
                    MystcraftComponents.IS_CREATIVE_SPAWNED.get(),
                    IsCreativeSpawnedComponent(true)
                )

                patchedComponents.set(
                    MystcraftComponents.BIOME_SYMBOLS.get(),
                    BiomeSymbolsComponent(
                        server.registryAccess().registryOrThrow(Registries.BIOME).keySet().toList()
                    )
                )
                stack.applyComponentsAndValidate(patchedComponents.asPatch())
            }
        }
    }
}