package kd.bettervillagers;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

/**
 * Contains specified custom recipes.
 * 
 * @author Krzysztof "Sejoslaw" Dobrzynski
 *
 */
public class BetterVillagersTradeList implements ITradeList {

	private MerchantRecipeList _recipes;

	public BetterVillagersTradeList(MerchantRecipeList recipes) {
		this._recipes = recipes;
	}

	@Override
	public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
		for (MerchantRecipe recipe : this._recipes) {
			recipeList.add(recipe);
		}
	}
}