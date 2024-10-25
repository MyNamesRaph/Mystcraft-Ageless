package com.mynamesraph.mystcraft.data.datagen.block

import com.mynamesraph.mystcraft.Mystcraft
import com.mynamesraph.mystcraft.block.crystal.CrystalColor
import com.mynamesraph.mystcraft.registry.MystcraftBlocks
import net.minecraft.client.renderer.RenderType
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RotatedPillarBlock
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.client.model.generators.ModelFile.ExistingModelFile
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.registries.DeferredBlock

class MystcraftBlockStateProvider(
    output: PackOutput,
    val exFileHelper: ExistingFileHelper
) : BlockStateProvider(output, Mystcraft.MOD_ID, exFileHelper) {

    override fun registerStatesAndModels() {

        portalWithItem(MystcraftBlocks.LINK_PORTAL)

        //TODO: Disable AO on all of these
        registerCrystalStatesAndModels(
            MystcraftBlocks.BLUE_CRYSTAL_BLOCK,
            MystcraftBlocks.BUDDING_BLUE_CRYSTAL,
            MystcraftBlocks.SMALL_BLUE_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_BLUE_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_BLUE_CRYSTAL_BUD,
            MystcraftBlocks.BLUE_CRYSTAL_CLUSTER,
            CrystalColor.BLUE
        )

        directionalBlock(
            MystcraftBlocks.BlUE_BOOK_RECEPTACLE.get(),
            UncheckedModelFile(
                ResourceLocation.fromNamespaceAndPath(
                    Mystcraft.MOD_ID,
                    "block/blue_book_receptacle",
                )
            )
        )

        registerCrystalStatesAndModels(
            MystcraftBlocks.YELLOW_CRYSTAL_BLOCK,
            MystcraftBlocks.BUDDING_YELLOW_CRYSTAL,
            MystcraftBlocks.SMALL_YELLOW_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_YELLOW_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_YELLOW_CRYSTAL_BUD,
            MystcraftBlocks.YELLOW_CRYSTAL_CLUSTER,
            CrystalColor.YELLOW
        )

        registerCrystalStatesAndModels(
            MystcraftBlocks.GREEN_CRYSTAL_BLOCK,
            MystcraftBlocks.BUDDING_GREEN_CRYSTAL,
            MystcraftBlocks.SMALL_GREEN_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_GREEN_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_GREEN_CRYSTAL_BUD,
            MystcraftBlocks.GREEN_CRYSTAL_CLUSTER,
            CrystalColor.GREEN
        )

        registerCrystalStatesAndModels(
            MystcraftBlocks.PINK_CRYSTAL_BLOCK,
            MystcraftBlocks.BUDDING_PINK_CRYSTAL,
            MystcraftBlocks.SMALL_PINK_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_PINK_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_PINK_CRYSTAL_BUD,
            MystcraftBlocks.PINK_CRYSTAL_CLUSTER,
            CrystalColor.PINK
        )

        registerCrystalStatesAndModels(
            MystcraftBlocks.RED_CRYSTAL_BLOCK,
            MystcraftBlocks.BUDDING_RED_CRYSTAL,
            MystcraftBlocks.SMALL_RED_CRYSTAL_BUD,
            MystcraftBlocks.MEDIUM_RED_CRYSTAL_BUD,
            MystcraftBlocks.LARGE_RED_CRYSTAL_BUD,
            MystcraftBlocks.RED_CRYSTAL_CLUSTER,
            CrystalColor.RED
        )
    }
    
    private fun registerCrystalStatesAndModels(
        block: DeferredBlock<Block>,
        budding: DeferredBlock<Block>,
        small: DeferredBlock<Block>,
        medium: DeferredBlock<Block>,
        large: DeferredBlock<Block>,
        cluster: DeferredBlock<Block>,
        color: CrystalColor
        ) {
        cubeWithItem(block)
        cubeWithItem(budding)
        
        clusterBlock(small,color)
        clusterBlock(medium,color)
        clusterBlock(large,color)
        clusterBlock(cluster,color)
    }
    

    private fun cubeWithItem(block: DeferredBlock<*>) {
        simpleBlockWithItem(block.get(),cubeAll(block.get()))
    }

    private fun portalWithItem(block: DeferredBlock<*>) {
        simpleBlockItem(block.get(),cubeAll(block.get()))

        val blockName = block.registeredName.replace("mystcraft_ageless:","")

        val texture = ResourceLocation.fromNamespaceAndPath(
            "mystcraft_ageless",
            "block/$blockName"
        )

        simpleBlock(block.get(),models().cubeAll(blockName,texture).renderType("minecraft:"+RenderType.TRANSLUCENT.name))
    }

    private fun clusterBlock(block: DeferredBlock<*>, color: CrystalColor) {

        val blockName = block.registeredName.replace("mystcraft_ageless:","")

        val texture = ResourceLocation.fromNamespaceAndPath(
            "mystcraft_ageless",
            "block/crystal_cluster/${color.name.lowercase()}/$blockName"
        )

        directionalBlock(
            block.get(),
            models().cross(
                blockName,
                texture
            ).renderType("minecraft:cutout_mipped")
        )
    }
}