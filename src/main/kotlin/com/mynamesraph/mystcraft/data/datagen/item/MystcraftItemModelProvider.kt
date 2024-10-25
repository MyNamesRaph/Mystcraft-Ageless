package com.mynamesraph.mystcraft.data.datagen.item

import com.mynamesraph.mystcraft.Mystcraft
import com.mynamesraph.mystcraft.block.crystal.CrystalColor
import com.mynamesraph.mystcraft.registry.MystcraftBlocks
import com.mynamesraph.mystcraft.registry.MystcraftItems
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem

class MystcraftItemModelProvider(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : ItemModelProvider(output, Mystcraft.MOD_ID, existingFileHelper) {

    override fun registerModels() {
        basicItem(MystcraftItems.LINKING_BOOK.get())

        registerCrystalModels(
            MystcraftItems.BLUE_CRYSTAL,
            MystcraftBlocks.SMALL_BLUE_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_BLUE_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_BLUE_CRYSTAL_BUD,
            MystcraftBlocks.BLUE_CRYSTAL_CLUSTER,
            CrystalColor.BLUE
        )

        simpleBlockItem(MystcraftBlocks.BlUE_BOOK_RECEPTACLE.get())

        registerCrystalModels(
            MystcraftItems.YELLOW_CRYSTAL,
            MystcraftBlocks.SMALL_YELLOW_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_YELLOW_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_YELLOW_CRYSTAL_BUD,
            MystcraftBlocks.YELLOW_CRYSTAL_CLUSTER,
            CrystalColor.YELLOW
        )

        registerCrystalModels(
            MystcraftItems.GREEN_CRYSTAL,
            MystcraftBlocks.SMALL_GREEN_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_GREEN_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_GREEN_CRYSTAL_BUD,
            MystcraftBlocks.GREEN_CRYSTAL_CLUSTER,
            CrystalColor.GREEN
        )

        registerCrystalModels(
            MystcraftItems.PINK_CRYSTAL,
            MystcraftBlocks.SMALL_PINK_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_PINK_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_PINK_CRYSTAL_BUD,
            MystcraftBlocks.PINK_CRYSTAL_CLUSTER,
            CrystalColor.PINK
        )

        registerCrystalModels(
            MystcraftItems.RED_CRYSTAL,
            MystcraftBlocks.SMALL_RED_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_RED_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_RED_CRYSTAL_BUD,
            MystcraftBlocks.RED_CRYSTAL_CLUSTER,
            CrystalColor.RED
        )
    }

    fun registerCrystalModels(
        item: DeferredItem<Item>,
        small: DeferredBlock<Block>,
        medium: DeferredBlock<Block>,
        large: DeferredBlock<Block>,
        cluster: DeferredBlock<Block>,
        color: CrystalColor
    ) {
        basicItem(item.get())
        budBlockItem(small,color)
        budBlockItem(medium,color)
        budBlockItem(large,color)
        flatBlockItem(cluster,color)

    }

    fun budBlockItem(block: DeferredBlock<*>,color: CrystalColor): ItemModelBuilder {
        val blockName = block.registeredName.replace("${Mystcraft.MOD_ID}:","")

        val texture = ResourceLocation.fromNamespaceAndPath(
            "mystcraft_ageless",
            "block/crystal_cluster/${color.name.lowercase()}/$blockName"
        )

        return getBuilder(blockName)
            .parent(UncheckedModelFile("item/amethyst_bud"))
            .texture("layer0", texture)
    }

    fun flatBlockItem(block: DeferredBlock<*>,color: CrystalColor): ItemModelBuilder {
        val blockName = block.registeredName.replace("${Mystcraft.MOD_ID}:","")

        val texture = ResourceLocation.fromNamespaceAndPath(
            "mystcraft_ageless",
            "block/crystal_cluster/${color.name.lowercase()}/$blockName"
        )

        return getBuilder(blockName)
            .parent(UncheckedModelFile("item/generated"))
            .texture("layer0", texture)
    }
}