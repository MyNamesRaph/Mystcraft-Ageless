package com.mynamesraph.mystcraft.item

import com.mynamesraph.mystcraft.component.LocationComponent
import com.mynamesraph.mystcraft.component.RotationComponent
import com.mynamesraph.mystcraft.registry.MystcraftComponents
import com.mynamesraph.mystcraft.ui.screen.LinkingBookScreen
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.RelativeMovement
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.portal.DimensionTransition
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

class LinkingBookItem(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        openScreen(level,player,usedHand)
        return super.use(level, player, usedHand)
    }

    private fun openScreen(level:Level,player: Player,usedHand: InteractionHand) {
        if(level.isClientSide) {
            Minecraft.getInstance()
                .setScreen(
                    LinkingBookScreen(
                        Component.literal("linking_book_screen"),
                        usedHand
                    )
                )
        }
    }

    fun teleportToLocationFromHand(level: Level, player: Player, usedHand: InteractionHand) {
        if(!level.isClientSide()) {
            val location = player.getItemInHand(usedHand).get(MystcraftComponents.LOCATION_COMPONENT.get())
            val rotation = player.getItemInHand(usedHand).get(MystcraftComponents.ROTATION_COMPONENT.get())

            if (location is LocationComponent && rotation is RotationComponent) {
                teleportToLocationFromLectern(level,player,location,rotation)
            }
        }
    }

    fun teleportToLocationFromLectern(level: Level, player: Player,location: LocationComponent,rotation: RotationComponent) {
        if(!level.isClientSide()) {
            var locationLevel:ServerLevel? = level.server!!.getLevel(location.levelKey)
            if (locationLevel == null) {
                locationLevel = level.server!!.getLevel(ServerLevel.OVERWORLD)

                System.err.println("Attempted teleporting to an Unknown dimension " + location.levelKey + " !")

                if (locationLevel!!.level != level) {
                    System.err.println("Safely teleporting player to the overworld!")
                    player.changeDimension(
                        DimensionTransition(
                            locationLevel,
                            locationLevel.sharedSpawnPos.toVec3(),
                            player.deltaMovement,
                            0.0f,
                            0.0f,
                            DimensionTransition.DO_NOTHING
                        )
                    )
                }
                return
            }

            if (locationLevel.level != level) {
                player.changeDimension(
                    DimensionTransition(
                        locationLevel,
                        location.position.toVec3(),
                        player.deltaMovement,
                        rotation.rotY,
                        rotation.rotX,
                        DimensionTransition.DO_NOTHING
                    )
                )
            }
            else {
                player.teleportTo(
                    locationLevel,
                    location.position.x.toDouble(),
                    location.position.y.toDouble(),
                    location.position.z.toDouble(),
                    setOf<RelativeMovement>(),
                    rotation.rotY,
                    rotation.rotX
                )
            }

        }


    }

}