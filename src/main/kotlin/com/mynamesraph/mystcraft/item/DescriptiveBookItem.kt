package com.mynamesraph.mystcraft.item

import com.mojang.serialization.DynamicOps
import com.mynamesraph.mystcraft.Mystcraft
import com.mynamesraph.mystcraft.component.DimensionIdentificatorComponent
import com.mynamesraph.mystcraft.data.saved.DimensionIdentificatorCounter
import com.mynamesraph.mystcraft.registry.MystcraftComponents
import com.mynamesraph.mystcraft.worldgen.flatNoiseRouter
import net.commoble.infiniverse.api.InfiniverseAPI
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.SurfaceRuleData
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.*
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.levelgen.NoiseSettings
import net.minecraft.world.level.levelgen.presets.WorldPresets
import net.minecraft.world.level.saveddata.SavedData.Factory


class DescriptiveBookItem(properties: Properties) : LinkingBookItem(properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (!level.isClientSide) {

            val counter = level.server!!.overworld().dataStorage.computeIfAbsent(Factory(this::create,this::load),DimensionIdentificatorCounter.FILE_NAME)

            val idComponent = player.getItemInHand(usedHand).components.get(MystcraftComponents.DIMENSION_ID_COMPONENT.get())

            if (idComponent is DimensionIdentificatorComponent) {
                if (!idComponent.generated) {
                    val levelKey = ResourceKey.create(
                        Registries.DIMENSION,
                        ResourceLocation.fromNamespaceAndPath(
                            Mystcraft.MOD_ID,
                            "age_${counter.id++}"
                        )
                    )
                    counter.setDirty()

                    InfiniverseAPI.get().getOrCreateLevel(
                        level.server,
                        levelKey
                    ) {
                        createNoiseLevel(level.server!!)
                    }
                }
            }


        }

        return super.use(level, player, usedHand)
    }

    private fun createSimpleLevel(server: MinecraftServer): LevelStem {
        val oldLevel = server.overworld()
        val ops: DynamicOps<Tag> = RegistryOps.create(NbtOps.INSTANCE, server.registryAccess())
        val oldChunkGenerator = oldLevel.chunkSource.generator
        val newChunkGenerator: ChunkGenerator = ChunkGenerator.CODEC.encodeStart(ops, oldChunkGenerator)
            .flatMap { ChunkGenerator.CODEC.parse(ops,it) }
            .getOrThrow { RuntimeException("Error copying dimension: $it") }
        val typeHolder: Holder<DimensionType> = oldLevel.dimensionTypeRegistration()
        return LevelStem(typeHolder, newChunkGenerator)
    }

    private  fun createNoiseLevel(server: MinecraftServer): LevelStem {
        //val ops: DynamicOps<Tag> = RegistryOps.create(NbtOps.INSTANCE, server.registryAccess())
        val dimensionType: Holder<DimensionType> = server.overworld().dimensionTypeRegistration()
        val preset = WorldPresets.getNormalOverworld(server.registryAccess())

        //val overworldNoiseSettings = NoiseSettings.create(-64, 384, 1, 2)

        val flatNoiseSettings = NoiseSettings.create(-64, 128, 1, 2)

        //val noiseSettings = NoiseSettings.create(128,128,1,1) cool skylands
        //val noiseSettings = NoiseSettings.create(-64,0,1,2)

        val generator = NoiseBasedChunkGenerator(
            preset.generator.biomeSource,
            Holder.direct(
                NoiseGeneratorSettings(
                    flatNoiseSettings,
                    Blocks.STONE.defaultBlockState(),
                    Blocks.WATER.defaultBlockState(),
                    flatNoiseRouter(
                        server.registryAccess().asGetterLookup().lookupOrThrow(Registries.DENSITY_FUNCTION),
                        server.registryAccess().asGetterLookup().lookupOrThrow(Registries.NOISE),
                        false
                    ),
                    SurfaceRuleData.overworld(),
                    OverworldBiomeBuilder().spawnTarget(),
                    0,
                    false,
                    false,
                    true,
                    false
                )
            )
        )

        return LevelStem(dimensionType,generator)

        //return WorldPresets.getNormalOverworld(server.registryAccess())
    }

    fun create(): DimensionIdentificatorCounter {
        return DimensionIdentificatorCounter()
    }

    fun load(tag: CompoundTag, lookupProvider: HolderLookup.Provider):DimensionIdentificatorCounter {
        val counter = this.create()

        if (tag.contains("count", Tag.TAG_INT.toInt())) {
            val count = tag.getInt("count")
            counter.id = count
        }

        return counter
    }

}