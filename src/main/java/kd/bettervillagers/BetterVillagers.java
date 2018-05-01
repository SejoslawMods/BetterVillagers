package kd.bettervillagers;

import java.io.FileNotFoundException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = BetterVillagers.MODID, version = BetterVillagers.VERSION, name = BetterVillagers.NAME)
public class BetterVillagers {
	public static final String MODID = "bettervillagers";
	public static final String VERSION = "@VERSION@";
	public static final String NAME = "Better Villagers";

	@Mod.Instance
	public static BetterVillagers INSTANCE;

	private static Logger LOGGER;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws FileNotFoundException {
		LOGGER = event.getModLog();
		BVConfig.preInit(event);
		log(Level.INFO, "Starting Better Villagers...");

		if (BVConfig.REMOVE_DEFAULT_TRADES) {
			log(Level.INFO, "Removing default trades...");
			BVDefaultTrades.removeDefaultTrades();
			log(Level.INFO, "Default trades removed.");
		}

		BVTradesStorage.refreshTrades();
		log(Level.INFO, "Custom trades loaded.");
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		log(Level.INFO, "Registering RefreshTrades command...");
		ICommand refreshTradesCommand = new RefreshTradesCommand();
		event.registerServerCommand(refreshTradesCommand);
		log(Level.INFO, "RefreshTrades command registered.");
	}

	public static void log(Level level, String message) {
		LOGGER.log(level, message);
	}
}