package com.mynamesraph.mystcraft.crafting.recipe

import com.mojang.logging.LogUtils
import com.mynamesraph.mystcraft.component.LocationComponent
import com.mynamesraph.mystcraft.component.RotationComponent
import com.mynamesraph.mystcraft.crafting.input.PlayerCraftingInput
import com.mynamesraph.mystcraft.registry.MystcraftComponents
import com.mynamesraph.mystcraft.registry.MystcraftRecipes
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.component.PatchedDataComponentMap
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.util.RecipeMatcher
import org.joml.Vector3f

/***
 * Shapeless recipe that sets a LocationComponent and a RotationComponent from the player to the output.
 */
class LocationalShapelessRecipe(
    val result: ItemStack,
    val ingredientsList: MutableList<Ingredient>
): Recipe<PlayerCraftingInput> {

    private val isSimple:Boolean = ingredientsList.stream().allMatch(Ingredient::isSimple)

    private val lazyIngredients: NonNullList<Ingredient> by lazy {
        val list = NonNullList.create<Ingredient>()
        for (ingredient in ingredientsList) {
            list.add(ingredient)
        }
        list
    }

    override fun assemble(input: PlayerCraftingInput, registries: HolderLookup.Provider): ItemStack {
        val itemStack = result.copy()
        val patchedComponents = PatchedDataComponentMap(itemStack.components)

        if (patchedComponents.get(MystcraftComponents.LOCATION_COMPONENT.get()) != null) {
            patchedComponents.set(
                MystcraftComponents.LOCATION_COMPONENT.get(),
                LocationComponent(
                    input.player!!.level().dimension(),
                    Vector3f(
                        input.player.x.toFloat(),
                        input.player.y.toFloat(),
                        input.player.z.toFloat()
                    )))
        }
        else {
            LogUtils.getLogger().error(
                "Attempted to craft an item that does not have a ${LocationComponent::class.simpleName} with a ${this::class.simpleName}." +
                        "Please add the component as a default component to the item before using this recipe."
            )
        }

        if (patchedComponents.get(MystcraftComponents.ROTATION_COMPONENT.get()) != null) {
            patchedComponents.set(
                MystcraftComponents.ROTATION_COMPONENT.get(),
                RotationComponent(
                    input.player!!.xRot,
                    input.player.yRot
                )
            )
        }
        else {
            LogUtils.getLogger().error(
                "Attempted to craft an item that does not have a ${RotationComponent::class.simpleName} with a ${this::class.simpleName}." +
                        "Please add the component as a default component to the item before using this recipe."
            )
        }

        itemStack.applyComponentsAndValidate(patchedComponents.asPatch())
        return itemStack
    }

    override fun getIngredients(): NonNullList<Ingredient>  {
        return lazyIngredients
    }

    override fun matches(input: PlayerCraftingInput, level: Level): Boolean {
        if (input.ingredientCount != this.ingredientsList.size) {
            return false
        } else if (!isSimple) {
            val nonEmptyItems = ArrayList<ItemStack>(input.ingredientCount)
            for (item in input.items) if (!item.isEmpty) nonEmptyItems.add(item)
            return RecipeMatcher.findMatches(nonEmptyItems, this.ingredientsList) != null
        } else {
            return if (input.size() == 1 && this.ingredientsList.size == 1
            ) this.ingredientsList.first().test(input.getItem(0))
            else input.stackedContents.canCraft(this, null)
        }
    }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean {
        return width * height >= this.ingredientsList.size
    }

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack {
        return result
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return MystcraftRecipes.LOCATIONAL_SHAPELESS_RECIPE_SERIALIZER.get()
    }

    override fun getType(): RecipeType<*> {
        return MystcraftRecipes.LOCATIONAL_RECIPE_TYPE.get()
    }

    override fun isSpecial(): Boolean {
        return true
    }

    override fun getRemainingItems(input: PlayerCraftingInput): NonNullList<ItemStack> {
        return NonNullList.withSize(input.size(), ItemStack.EMPTY)
    }
}
