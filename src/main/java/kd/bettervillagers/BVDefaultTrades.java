package kd.bettervillagers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

/**
 * Module which is responsible for removing Villagers default trades.
 * 
 * @author Krzysztof "Sejoslaw" Dobrzynski
 * 
 */
public class BVDefaultTrades {

	/**
	 * Removes all Villagers default trades.
	 */
	public static void removeDefaultTrades() {
		for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS) {
			removeDefaultTradesFromProfession(profession);
		}
	}

	/**
	 * Removes all trades from specified VillagerProfession.
	 */
	private static void removeDefaultTradesFromProfession(VillagerProfession profession) {
		try {
			Field careersField = profession.getClass().getDeclaredField("careers");
			careersField.setAccessible(true);
			List<VillagerCareer> careers = (List<VillagerCareer>) careersField.get(profession);

			for (VillagerCareer career : careers) {
				removeDefaultTradesFromCareer(profession, career);
			}
		} catch (Exception e) {
			BetterVillagers.log(Level.ERROR, "Error when removing trades from profession.");
			e.printStackTrace();
		}
	}

	/**
	 * Removes all trades from specified VillagerCareer.
	 */
	private static void removeDefaultTradesFromCareer(VillagerProfession profession, VillagerCareer career) {
		try {
			Field tradesField = career.getClass().getDeclaredField("trades");
			tradesField.setAccessible(true);
			List<List<ITradeList>> trades = (List<List<ITradeList>>) tradesField.get(career);
			trades.clear();
		} catch (Exception e) {
			BetterVillagers.log(Level.ERROR, "Error when removing trades from career.");
			e.printStackTrace();
		}
	}
}