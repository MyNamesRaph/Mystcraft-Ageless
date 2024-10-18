package com.mynamesraph.mystcraft.data.networking.handlers

import com.mynamesraph.mystcraft.data.networking.packet.LinkingBookTravelPacket
import com.mynamesraph.mystcraft.item.LinkingBookItem
import net.neoforged.neoforge.network.handling.IPayloadContext

class MystCraftClientPayloadHandler {
    companion object {

        fun handleLinkingBookButtonPressed(data: LinkingBookTravelPacket, context: IPayloadContext) {
            println("Received Packet From Server: $data")

            val player = context.player()
            val level = player.level()
            val item = player.getItemInHand(data.interactionHand).item

            if (item is LinkingBookItem) {
                item.teleportToLocationFromHand(level,player,data.interactionHand)
            }
        }
    }
}