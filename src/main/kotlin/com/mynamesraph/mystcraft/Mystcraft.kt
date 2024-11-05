package com.mynamesraph.mystcraft

import com.mojang.logging.LogUtils
import com.mynamesraph.mystcraft.block.portal.LinkPortalBlock
import com.mynamesraph.mystcraft.data.networking.handlers.MystCraftServerPayloadHandler
import com.mynamesraph.mystcraft.data.networking.packet.LinkingBookLecternTravelPacket
import com.mynamesraph.mystcraft.data.networking.packet.LinkingBookTravelPacket
import com.mynamesraph.mystcraft.data.networking.packet.WritingDeskRenamePacket
import com.mynamesraph.mystcraft.data.networking.packet.WritingDeskSymbolPacket
import com.mynamesraph.mystcraft.registry.*
import com.mynamesraph.mystcraft.registry.MystcraftMenus.LINKING_BOOK_MENU
import com.mynamesraph.mystcraft.registry.MystcraftMenus.WRITING_DESK_MENU
import com.mynamesraph.mystcraft.ui.screen.LecternLinkingBookScreen
import com.mynamesraph.mystcraft.ui.screen.WritingDeskScreen
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import java.util.function.Consumer


@Mod(Mystcraft.MOD_ID)
class Mystcraft(modEventBus: IEventBus, modContainer: ModContainer) {
    companion object {
        const val MOD_ID = "mystcraft_ageless"
        private val LOGGER = LogUtils.getLogger()

        // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
        @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
        object ClientModEvents {
            @SubscribeEvent
            fun onClientSetup(event: FMLClientSetupEvent?) {
                // Some client setup code
                LOGGER.info("HELLO FROM CLIENT SETUP")
                LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().user.name)
            }

            @SubscribeEvent
            fun register(event: RegisterPayloadHandlersEvent) {
                val payloadRegistrar: PayloadRegistrar = event.registrar("1")
                payloadRegistrar.playToServer(
                    LinkingBookTravelPacket.TYPE,
                    LinkingBookTravelPacket.STREAM_CODEC,
                    MystCraftServerPayloadHandler::handleLinkingBookButtonPressed
                )
                payloadRegistrar.playToServer(
                    LinkingBookLecternTravelPacket.TYPE,
                    LinkingBookLecternTravelPacket.STREAM_CODEC,
                    MystCraftServerPayloadHandler::handleLecternLinkingBookButtonPressed
                )
                payloadRegistrar.playToServer(
                    WritingDeskRenamePacket.TYPE,
                    WritingDeskRenamePacket.STREAM_CODEC,
                    MystCraftServerPayloadHandler::handleWritingDeskRenamedBook
                )
                payloadRegistrar.playToServer(
                    WritingDeskSymbolPacket.TYPE,
                    WritingDeskSymbolPacket.STREAM_CODEC,
                    MystCraftServerPayloadHandler::handleWritingDeskAddingSymbol
                )
            }

            @SubscribeEvent
            fun registerScreens(event: RegisterMenuScreensEvent) {
                event.register(LINKING_BOOK_MENU.get(),::LecternLinkingBookScreen)
                event.register(WRITING_DESK_MENU.get(),::WritingDeskScreen)
            }

            @SubscribeEvent
            fun registerBlockColorHandlers(event: RegisterColorHandlersEvent.Block) {
                event.register(
                    { blockState: BlockState, _: BlockAndTintGetter?, _: BlockPos?, _: Int ->
                        if (blockState.block is LinkPortalBlock) {
                            if (ModList.get().isLoaded("past_el_palettes")) {
                                if (blockState.getValue(LinkPortalBlock.IS_PASTEL_COLOR)) {
                                    blockState.getValue(LinkPortalBlock.PASTEL_COLOR!!).textColor
                                }
                                else {
                                    blockState.getValue(LinkPortalBlock.COLOR).textColor
                                }
                            } else {
                                blockState.getValue(LinkPortalBlock.COLOR).textColor
                            }
                        }
                        else {
                            DyeColor.BLACK.textColor
                        }
                    },
                MystcraftBlocks.LINK_PORTAL.get()
                )
            }
        }

        @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.DEDICATED_SERVER])
        object ServerModEvents {
            @SubscribeEvent
            fun onClientSetup(event: FMLClientSetupEvent?) {
                // Some server setup code
                LOGGER.info("HELLO FROM SERVER SETUP")
            }

            @SubscribeEvent
            fun register(event: RegisterPayloadHandlersEvent) {
                val payloadRegistrar: PayloadRegistrar = event.registrar("1")
                payloadRegistrar.playToServer(
                    LinkingBookTravelPacket.TYPE,
                    LinkingBookTravelPacket.STREAM_CODEC,
                    MystCraftServerPayloadHandler::handleLinkingBookButtonPressed
                )
                payloadRegistrar.playToServer(
                    LinkingBookLecternTravelPacket.TYPE,
                    LinkingBookLecternTravelPacket.STREAM_CODEC,
                    MystCraftServerPayloadHandler::handleLecternLinkingBookButtonPressed
                )
                payloadRegistrar.playToServer(
                    WritingDeskRenamePacket.TYPE,
                    WritingDeskRenamePacket.STREAM_CODEC,
                    MystCraftServerPayloadHandler::handleWritingDeskRenamedBook
                )
                payloadRegistrar.playToServer(
                    WritingDeskSymbolPacket.TYPE,
                    WritingDeskSymbolPacket.STREAM_CODEC,
                    MystCraftServerPayloadHandler::handleWritingDeskAddingSymbol
                )
            }

            @SubscribeEvent
            fun registerScreens(event: RegisterMenuScreensEvent) {
                event.register(LINKING_BOOK_MENU.get(),::LecternLinkingBookScreen)
                event.register(WRITING_DESK_MENU.get(),::WritingDeskScreen)
            }
        }
    }

    init {
        modEventBus.addListener(::commonSetup)
        MystcraftBlocks.register(modEventBus)
        MystcraftComponents.register(modEventBus)
        MystcraftItems.register(modEventBus)
        MystcraftBlockEntities.register(modEventBus)
        MystcraftTabs.register(modEventBus)
        MystcraftMenus.register(modEventBus)
        MystcraftRecipes.register(modEventBus)
        NeoForge.EVENT_BUS.register(this)
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP")

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT))

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber)

        Config.items.forEach(Consumer { item: Item ->
            LOGGER.info(
                "ITEM >> {}",
                item.toString()
            )
        })
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting")
    }


}