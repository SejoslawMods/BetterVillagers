package kd.bettervillagers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

/*
 * Main module which is responsible for loading and process custom trades.
 */
public class BVTradesStorage {

	private static final String PROFESSION_KEY = "Profession";
	private static final String CAREER_KEY = "Career";
	private static final String TRADING_KEY = "Trading";
	private static final String LEVEL_KEY = "Level";
	private static final String TRADES_KEY = "Trades";

	private BVTradesStorage() {
	}

	/*
	 * Main method to reload custom trades.
	 */
	public static void refreshTrades() {
		JsonArray sections = getMainTradesSections();
		if (sections != null) {
			for (JsonElement section : sections) {
				processSection(section);
			}
		}
	}

	/*
	 * Processes single section. Single section contains Profession, Career and
	 * Trading.
	 */
	private static void processSection(JsonElement section) {
		JsonObject sectionObject = section.getAsJsonObject();
		JsonElement professionElement = sectionObject.get(PROFESSION_KEY);
		VillagerProfession profession = getProfession(professionElement);

		if (profession != null) {
			JsonElement careerElement = sectionObject.get(CAREER_KEY);
			VillagerCareer career = getCareer(careerElement, profession);

			if (career != null) {
				handleTrading(sectionObject, career);
			} else {
				BetterVillagers.log(Level.ERROR, "Unknown career: " + careerElement);
			}
		} else {
			BetterVillagers.log(Level.ERROR, "Unknown profession: " + professionElement);
		}
	}

	/*
	 * Processes trading (level and trades) for single section using parsed
	 * Profession and Career.
	 */
	private static void handleTrading(JsonObject section, VillagerCareer career) {
		JsonElement tradingElement = section.get(TRADING_KEY);
		JsonArray tradingArray = tradingElement.getAsJsonArray();

		for (JsonElement tradesElement : tradingArray) {
			JsonObject tradesObject = tradesElement.getAsJsonObject();
			handleSingleTradingSection(tradesObject, career);
		}
	}

	/*
	 * Single trade section contains trade level and array of trades.
	 */
	private static void handleSingleTradingSection(JsonObject tradesObject, VillagerCareer career) {
		int level = tradesObject.get(LEVEL_KEY).getAsInt();

		JsonObject recipesObject = tradesObject.get(TRADES_KEY).getAsJsonObject();
		MerchantRecipeList recipes = parseRecipes(recipesObject);

		BetterVillagersTradeList tradeList = new BetterVillagersTradeList(recipes);
		career.addTrade(level, tradeList);
	}

	/*
	 * @return Returns MerchantRecipeList with parsed recipes from single Trades
	 * section.
	 */
	private static MerchantRecipeList parseRecipes(JsonObject tradesObject) {
		MerchantRecipeList recipeList = new MerchantRecipeList();

		Set<Map.Entry<String, JsonElement>> tradesSet = tradesObject.entrySet();
		for (Map.Entry<String, JsonElement> tradeEntry : tradesSet) {
			parseAndAddSingleRecipe(tradeEntry, recipeList);
		}

		return recipeList;
	}

	/*
	 * Reads trade from Entry and adds it to MerchantRecipeList.
	 */
	private static void parseAndAddSingleRecipe(Entry<String, JsonElement> tradeEntry, MerchantRecipeList recipeList) {
		String tradeName = tradeEntry.getKey();
		if (!tradeName.contains(BVConfig.COMMENT)) {
			String trade = tradeEntry.getValue().getAsString();
			MerchantRecipe recipe = parseTradeToMerchantRecipe(trade);
			recipeList.add(recipe);
		}
	}

	/*
	 * @return Returns parsed MerchantRecipe from given trade string.
	 */
	private static MerchantRecipe parseTradeToMerchantRecipe(String trade) {
		String tradeTrimmed = removeWhitespaces(trade);

		String[] equalityParts = tradeTrimmed.split("=");
		String addition = equalityParts[0];
		String result = equalityParts[1];

		String[] additionParts = addition.split("\\+");
		String buyItem1 = additionParts[0]; // There is at least one item which Villager must be able to buy

		MerchantRecipe merchantRecipe = null;
		if (additionParts.length > 1) {
			String buyItem2 = additionParts[1];
			merchantRecipe = parseTradeToMerchantRecipe(buyItem1, buyItem2, result);
		} else {
			merchantRecipe = parseTradeToMerchantRecipe(buyItem1, null, result);
		}

		return merchantRecipe;
	}

	private static String removeWhitespaces(String str) {
		String withourWhitespaces = str.replaceAll("\\s+", "");
		return withourWhitespaces;
	}

	/*
	 * @return Returns parsed MerchantRecipe from specified trade parts. Each part
	 * is in one of those forms: "1x1:1" or "1xminecraft:stone".
	 */
	private static MerchantRecipe parseTradeToMerchantRecipe(String buyItem1, String buyItem2, String result) {
		ItemStack buyItem1Stack = getItemStackFromTradePart(buyItem1);
		ItemStack buyItem2Stack = getItemStackFromTradePart(buyItem2);
		ItemStack resultStack = getItemStackFromTradePart(result);

		MerchantRecipe merchantRecipe = new MerchantRecipe(buyItem1Stack, buyItem2Stack, resultStack);
		return merchantRecipe;
	}

	/*
	 * @return Returns ItemStack from given trade part. Specified part is in one of
	 * those forms: "1x1:1" or "1xminecraft:stone".
	 */
	private static ItemStack getItemStackFromTradePart(String tradePart) {
		if (tradePart == null) {
			return ItemStack.EMPTY;
		}

		String tradeTrimmed = removeWhitespaces(tradePart);

		String[] stackDetails = tradePart.split("x");
		String stackSizeInfo = stackDetails[0]; // This should be a number from 1 to 64
		String itemInfo = stackDetails[1]; // Information about item or block in form of "1:1" or "minecraft:stone"

		String[] itemInfoParts = itemInfo.split(":");

		ItemStack stack = null;
		int stackSize = Integer.parseInt(stackSizeInfo);

		try { // integer version - for example: "1:1"
			int itemId = Integer.parseInt(itemInfoParts[0]);
			int metadataId = 0;

			if (itemInfoParts.length > 1) {
				metadataId = Integer.parseInt(itemInfoParts[1]);
			}

			Item item = Item.getItemById(itemId);
			stack = new ItemStack(item, stackSize, metadataId);
		} catch (Exception e) { // string version - for example: "minecraft:stone"
			ResourceLocation resLoc = new ResourceLocation(itemInfo);

			Item item = Item.REGISTRY.getObject(resLoc);
			stack = new ItemStack(item, stackSize);
		}

		return stack;
	}

	/*
	 * @return Returns VillagerCareer based on what the specified element holds.
	 * Vllager Career sohuld be possible to read from 2 ways: "Career":1 - as int,
	 * or "Career":"Farmer" - as string
	 */
	private static VillagerCareer getCareer(JsonElement careerElement, VillagerProfession profession) {
		String careerValue = careerElement.getAsString();
		VillagerCareer career = null;

		try {
			int careerId = Integer.parseInt(careerValue);
			career = profession.getCareer(careerId);
		} catch (Exception e) {
			career = parseCareerFromName(careerValue, profession);
		}

		return career;
	}

	/*
	 * @return Returns VillagerCareer from it's specified name.
	 */
	private static VillagerCareer parseCareerFromName(String careerName, VillagerProfession profession) {
		int index = 1;

		VillagerCareer defaultCareer = profession.getCareer(0);
		VillagerCareer actualCareer = profession.getCareer(index);

		while (true) {
			if (actualCareer.getName().equalsIgnoreCase(careerName)) {
				return actualCareer;
			} else if (actualCareer.getName().equalsIgnoreCase(defaultCareer.getName())) {
				BetterVillagers.log(Level.WARN, "Didn't found a career from value: " + careerName);
				return null;
			}

			index++;
			actualCareer = profession.getCareer(index);
		}
	}

	/*
	 * @return Returns VillagerProfession based on what the specified element holds.
	 * Specified element may be in 2 ways: "Profession":5 - as int, or
	 * "Profession":"minecraft:nitwit" - as string.
	 */
	private static VillagerProfession getProfession(JsonElement professionElement) {
		String professionValue = professionElement.getAsString();
		VillagerProfession villagerProfession = null;

		try { // First option - int value
			int professionId = Integer.parseInt(professionValue);
			villagerProfession = VillagerRegistry.getById(professionId);
		} catch (Exception e) { // Second option - string value
			ResourceLocation resLoc = new ResourceLocation(professionValue);
			villagerProfession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(resLoc);
		}

		return villagerProfession;
	}

	/*
	 * @return Returns full sections from file. Single section contains Profession,
	 * Career and Trading.
	 */
	private static JsonArray getMainTradesSections() {
		try {
			FileInputStream fis = new FileInputStream(BVConfig.TRADES_FILE);
			InputStreamReader reader = new InputStreamReader(fis);
			JsonParser parser = new JsonParser();
			JsonElement js = parser.parse(reader);

			if (js.isJsonArray()) {
				JsonArray ja = js.getAsJsonArray();
				return ja;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			BetterVillagers.log(Level.ERROR, "Error while loading JSON file with trades.");
		}
		BetterVillagers.log(Level.ERROR, "JSON File with trades wasn't load.");
		return null;
	}
}