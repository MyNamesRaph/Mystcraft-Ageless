package com.mynamesraph.mystcraft.item

import com.mynamesraph.mystcraft.component.LocationComponent
import com.mynamesraph.mystcraft.component.LocationDisplayComponent
import com.mynamesraph.mystcraft.component.RotationComponent
import com.mynamesraph.mystcraft.registry.MystcraftComponents
import com.mynamesraph.mystcraft.ui.screen.LinkingBookScreen
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.portal.DimensionTransition
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

open class LinkingBookItem(properties: Properties) : Item(properties) {

    override fun getHighlightTip(item: ItemStack, displayName: Component): Component {
        if (item.has(MystcraftComponents.LOCATION_DISPLAY)) {
            val display = item.components.get(MystcraftComponents.LOCATION_DISPLAY.get())

            if (display is LocationDisplayComponent) {
                return display.name.plainCopy()
            }
        }
        return super.getHighlightTip(item, displayName)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val display = stack.components.get(MystcraftComponents.LOCATION_DISPLAY.get())

        if (display is LocationDisplayComponent) {
            tooltipComponents.add(display.name)
        }
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        openScreen(level,player,usedHand)
        return super.use(level, player, usedHand)
    }

    protected open fun openScreen(level:Level, player: Player, usedHand: InteractionHand) {
        if(level.isClientSide) {
            Minecraft.getInstance()
                .setScreen(
                    LinkingBookScreen(
                        Component.literal("linking_book_screen"),
                        usedHand,
                        player
                    )
                )
        }
    }

    open fun teleportToLocationFromHand(level: Level, player: Player, usedHand: InteractionHand) {
        if(!level.isClientSide()) {

            val location = player.getItemInHand(usedHand).get(MystcraftComponents.LOCATION.get())
            val rotation = player.getItemInHand(usedHand).get(MystcraftComponents.ROTATION.get())

            if (location is LocationComponent && rotation is RotationComponent) {
                teleportToLocationFromLectern(level,player,location,rotation)
            }
        }
    }

    open fun teleportToLocationFromLectern(level: Level, entity: Entity,location: LocationComponent,rotation: RotationComponent) {
        if(!level.isClientSide()) {
            var locationLevel:ServerLevel? = level.server!!.getLevel(location.levelKey)
            if (locationLevel == null) {
                locationLevel = level.server!!.getLevel(ServerLevel.OVERWORLD)

                System.err.println("Attempted teleporting to an Unknown dimension " + location.levelKey + " !")

                if (locationLevel!!.level != level) {
                    System.err.println("Safely teleporting player to the overworld!")
                    entity.changeDimension(
                        DimensionTransition(
                            locationLevel,
                            locationLevel.sharedSpawnPos.toVec3(),
                            entity.deltaMovement,
                            0.0f,
                            0.0f,
                            DimensionTransition.DO_NOTHING
                        )
                    )
                }
                return
            }

            entity.changeDimension(
                DimensionTransition(
                    locationLevel,
                    location.position.toVec3(),
                    entity.deltaMovement,
                    rotation.rotY,
                    rotation.rotX,
                    DimensionTransition.DO_NOTHING
                )
            )
        }
    }

    open fun getDestination(level: Level, entity: Entity,location: LocationComponent,rotation: RotationComponent): DimensionTransition {
        var locationLevel:ServerLevel? = level.server!!.getLevel(location.levelKey)
        if (locationLevel == null) {
            locationLevel = level.server!!.getLevel(ServerLevel.OVERWORLD)

            System.err.println("Attempted teleporting to an Unknown dimension " + location.levelKey + " !")

            if (locationLevel!!.level != level) {
                System.err.println("Safely teleporting player to the overworld!")
                return DimensionTransition(
                        locationLevel,
                        locationLevel.sharedSpawnPos.toVec3(),
                        entity.deltaMovement,
                        0.0f,
                        0.0f,
                        DimensionTransition.DO_NOTHING
                    )
            }
        }
         return DimensionTransition(
                locationLevel,
                location.position.toVec3(),
                entity.deltaMovement,
                rotation.rotY,
                rotation.rotX,
                DimensionTransition.DO_NOTHING
        )
    }
}