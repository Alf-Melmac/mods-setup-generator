package de.webalf.service;

import com.opencsv.bean.CsvToBeanBuilder;
import de.webalf.exception.InvalidFileException;
import de.webalf.exception.WriteException;
import de.webalf.model.WorkshopItem;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.webalf.Constants.*;

/**
 * @author Alf
 * @since 15.11.2022
 */
public final class CSVParser {
	private CSVParser() {
	}

	public static void parse(String fullFileName) throws InvalidFileException, WriteException {
		final Map<Long, String> modMap = new HashMap<>();

		final String fileName = fullFileName.substring(0, fullFileName.length() - 4);

		try (FileReader reader = new FileReader(MOD_LOCATION + fullFileName)) {
			for (WorkshopItem workshopItem : new CsvToBeanBuilder<WorkshopItem>(reader)
					.withType(WorkshopItem.class)
					.build()) {
				modMap.put(workshopItem.getSteamid(), workshopItem.getDirectory());
			}
		} catch (IOException e) {
			throw new InvalidFileException("CSV Parsing error...", e);
		}

		System.out.println("Found " + modMap.size() + " mods in " + fullFileName);
		writeSetup(modMap, fileName);
	}

	private static void writeSetup(Map<Long, String> modMap, String fileName) throws WriteException {
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
			throw new WriteException("Failed to write setup " + fileName, e);
		}
	}
}
