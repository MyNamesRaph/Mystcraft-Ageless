package com.mynamesraph.mystcraft.data.networking.handlers

import com.mynamesraph.mystcraft.Mystcraft
import com.mynamesraph.mystcraft.component.LocationComponent
import com.mynamesraph.mystcraft.component.RotationComponent
import com.mynamesraph.mystcraft.data.networking.packet.LinkingBookLecternTravelPacket
import com.mynamesraph.mystcraft.data.networking.packet.LinkingBookTravelPacket
import com.mynamesraph.mystcraft.item.LinkingBookItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.LecternBlockEntity
import net.neoforged.neoforge.network.handling.IPayloadContext

class MystCraftServerPayloadHandler {
    companion object {
        fun handleLinkingBookButtonPressed(data: LinkingBookTravelPacket, context: IPayloadContext) {

            val player = context.player()
            val level = player.level()
            val item = player.getItemInHand(data.interactionHand).item

            println("Received Packet From ${player.uuid}: $data")

            if (item is LinkingBookItem) {
                item.teleportToLocationFromHand(level,player,data.interactionHand)
            }
        }

        fun handleLecternLinkingBookButtonPressed(data: LinkingBookLecternTravelPacket, context: IPayloadContext) {

            val player = context.player()
            val level = player.level()

            val lectern = level.getBlockEntity(data.pos)

            if (lectern is LecternBlockEntity) {
                val book = lectern.book.item

                if (book is LinkingBookItem) {

                    val location = lectern.book.components.get(
                        BuiltInRegistries.DATA_COMPONENT_TYPE.get(
                            ResourceLocation.fromNamespaceAndPath(Mystcraft.MOD_ID,"location")
                        )!!
                    )

                    val rotation = lectern.book.components.get(
                        BuiltInRegistries.DATA_COMPONENT_TYPE.get(
                            ResourceLocation.fromNamespaceAndPath(Mystcraft.MOD_ID,"rotation")
                        )!!
                    )

                    if (location is LocationComponent && rotation is RotationComponent) {
                        println("location:$location, rotation:$rotation")
                        book.teleportToLocationFromLectern(level,player,location,rotation)
                    }


                }
            }
        }
    }
}