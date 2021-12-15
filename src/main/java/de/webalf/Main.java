package de.webalf;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.*;
import java.util.*;

/**
 * @author Alf
 * @since 29.11.2021
 */
public class Main {
	private static final String MOD_LOCATION = "src/main/resources/mods/";

	private static final String MOD_PREFIX = "SET MOD%s=";
	private static final String MODNAME_PREFIX = "SET MODNAME%s=";

	private static final Set<Long> SKIP = Set.of(2430905589L /*TacControl*/, 708250744L /*ACEX*/);

	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			System.out.println("One argument needed");
			System.exit(400);
		}

		final String arg = args[0];

		if ("refresh".equals(arg)) {
			for (File file : new File(MOD_LOCATION).listFiles()) {
				final String fileName = file.getName();
				if (file.isFile() && fileName.endsWith(".csv")) {
					generateSetup(fileName);
				}
			}
		} else {
			generateSetup(arg);
		}
	}

	private static void generateSetup(String file) {
		final Map<Long, String> modMap = new HashMap<>();

		if (!file.endsWith(".csv")) {
			System.out.println("Can only parse csv files");
			System.exit(400);
		}
		final String fileName = file.substring(0, file.length() - 4);

		try (FileReader reader = new FileReader(MOD_LOCATION + file)) {
			for (WorkshopItem workshopItem : new CsvToBeanBuilder<WorkshopItem>(reader).withType(WorkshopItem.class).build()) {
				modMap.put(workshopItem.getSteamid(), workshopItem.getDirectory());
			}
			System.out.println("Found " + modMap.size() + " mods.");
		} catch (IOException e) {
			System.out.println("Fehler...");
			e.printStackTrace();
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(MOD_LOCATION + fileName + "-setup.txt"))) {
			List<String> directories = new ArrayList<>();

			int i = 1;
			for (Map.Entry<Long, String> modEntry : modMap.entrySet()) {
				final long steamId = modEntry.getKey();
				if (!SKIP.contains(steamId)) {
					writer.append(String.format(MOD_PREFIX, i)).append(String.valueOf(steamId));
					writer.newLine();
					directories.add(String.format(MODNAME_PREFIX, i) + modEntry.getValue());
				} else {
					directories.add(null);
				}
				i++;
			}

			writer.newLine();

			for (String directory : directories) {
				if (directory != null) {
					writer.append(directory);
					writer.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(500);
		}
	}
}
