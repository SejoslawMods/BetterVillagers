package kd.bettervillagers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.logging.log4j.Level;

/**
 * Unzips files from mods JAR file or /bin/ if in testing environment, directly
 * into config directory.
 * 
 * @author Krzysztof "Sejoslaw" Dobrzynski
 *
 */
public class FileUnzipper {

	private File _modConfigurationDirectory;
	private File _modFile;

	/**
	 * modConfigurationDirectory - config/Better Villagers/ <br>
	 * modFile - bettervillagers-[].jar OR /bin/ (if modding)
	 */
	public FileUnzipper(File modConfigurationDirectory, File modFile) {
		this._modConfigurationDirectory = modConfigurationDirectory;
		this._modFile = modFile;
		if (!this._modConfigurationDirectory.exists()) {
			this._modConfigurationDirectory.mkdirs();
			BetterVillagers.log(Level.INFO, "BetterVillagers config directory created");
		}
		BetterVillagers.log(Level.INFO, "BetterVillagers config directory loaded");
	}

	@SuppressWarnings("resource")
	public File unzip(String fileName, File localFile) {
		localFile = new File(_modConfigurationDirectory, fileName);
		if (!localFile.exists()) {
			try {
				localFile.createNewFile();
				if (!this._modFile.isDirectory()) {
					JarFile jarFile = new JarFile(this._modFile);
					Enumeration<JarEntry> enumeration = jarFile.entries();
					while (enumeration.hasMoreElements()) {
						JarEntry file = (JarEntry) enumeration.nextElement();
						if (file.getName().equals(fileName)) {
							InputStream is = jarFile.getInputStream(file);
							FileOutputStream fos = new FileOutputStream(localFile);
							while (is.available() > 0) {
								fos.write(is.read());
							}
							fos.close();
							is.close();
						}
					}
				} else if (this._modFile.isDirectory()) {
					File[] files = this._modFile.listFiles();
					for (File f : files) {
						if (f.getName().contains(fileName)) {
							FileInputStream is = new FileInputStream(f);
							FileOutputStream fos = new FileOutputStream(localFile);
							while (is.available() > 0) {
								fos.write(is.read());
							}
							fos.close();
							is.close();
						}
					}
				}
				BetterVillagers.log(Level.INFO, fileName + " created");
			} catch (IOException e) {
				e.printStackTrace();
				BetterVillagers.log(Level.INFO, "Error while creating " + fileName);
			}
		}
		return localFile;
	}
}