package kd.bettervillagers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.Level;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/*
 * Contains stuff related with mod configuration file.
 */
public class BVConfig {

	public static File CONFIG_DIRECTORY;
	public static File CONFIG_FILE;
	public static File TRADES_FILE;
	public static Configuration CONFIG;
	public static String COMMENT;
	public static String TRADES_FILENAME;

	public static final String TRADES_FILE_NAME = "Trades.json";
	public static final String BETTER_VILLAGERS_CFG = "BetterVillagers.cfg";
	public static FileUnzipper FILE_UNZIPPER;

	private BVConfig() {
	}

	public static void preInit(FMLPreInitializationEvent event) throws FileNotFoundException {
		FILE_UNZIPPER = new FileUnzipper(new File(event.getModConfigurationDirectory(), BetterVillagers.NAME),
				event.getSourceFile());

		CONFIG_DIRECTORY = new File(event.getModConfigurationDirectory(), BetterVillagers.NAME);
		if (!CONFIG_DIRECTORY.exists()) {
			CONFIG_DIRECTORY.mkdirs();
		}

		CONFIG_FILE = new File(CONFIG_DIRECTORY, BETTER_VILLAGERS_CFG);
		CONFIG = new Configuration(CONFIG_FILE);
		CONFIG.load();
		processConfig();
	}

	private static void processConfig() throws FileNotFoundException {
		TRADES_FILENAME = CONFIG.getString(TRADES_FILE_NAME, "Custom files", TRADES_FILE_NAME,
				"File from which all custom trades should be read.");
		TRADES_FILE = FILE_UNZIPPER.unzip(TRADES_FILENAME, TRADES_FILE);

		COMMENT = CONFIG.getString("Comment", "Comment", "_comment",
				"Any key which contains this value in name will be ignored while reading custom trades.");
	}
}