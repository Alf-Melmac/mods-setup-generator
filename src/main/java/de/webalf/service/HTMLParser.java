package de.webalf.service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import de.webalf.exception.InvalidFileException;
import de.webalf.exception.WriteException;
import de.webalf.model.WorkshopItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static de.webalf.Constants.MOD_LOCATION;

/**
 * @author Alf
 * @since 15.11.2022
 */
public final class HTMLParser {
	private static final String DATA_TYPE = "data-type";

	private HTMLParser() {
	}

	public static void parse(String fullFileName) throws InvalidFileException, WriteException {
		final File file = new File(MOD_LOCATION + fullFileName);
		Document document;
		try {
			document = Jsoup.parse(file);
		} catch (IOException e) {
			throw new InvalidFileException("HTML Parsing error...", e);
		}

		final Element modList = document.body().getElementsByTag("table").first();
		if (modList == null) throw new InvalidFileException("Invalid file " + fullFileName);
		Elements modContainers = modList.getElementsByAttributeValue(DATA_TYPE, "ModContainer");

		List<WorkshopItem> workshopItems = new ArrayList<>();
		for (Element modContainer : modContainers) {
			workshopItems.add(analyzeModContainer(modContainer));
		}

		System.out.println("Found " + workshopItems.size() + " mods in " + fullFileName);
		writeCsv(workshopItems, fullFileName.substring(0, fullFileName.length() - 5));
	}

	private static WorkshopItem analyzeModContainer(Element modContainer) {
		final Element displayNameEl = modContainer.getElementsByAttributeValue(DATA_TYPE, "DisplayName").first();
		if (displayNameEl == null) throw new InvalidFileException("Invalid file syntax - DisplayName");
		final String displayName = displayNameEl.ownText();

		final Element link = modContainer.getElementsByAttributeValue(DATA_TYPE, "Link").first();
		if (link == null) throw new InvalidFileException("Invalid file syntax - Link");
		final String href = link.attr("href");
		final String workshopId = href.substring(href.indexOf("=") + 1);

		final String directoryName = transformDisplayName(displayName);

		return new WorkshopItem(Long.parseLong(workshopId), displayName, directoryName);
	}

	private static String transformDisplayName(String displayName) {
		String ret = displayName;
		if (ret.contains(".fsm")) {
			ret = ret.substring(0, ret.indexOf(".fsm"));
		}
		// " - " or " | " or " / " or "."
		ret = ret.replaceAll("\\s?[-|/]\\s?|\\.", "-");
		// ":" or "(" or ")" or "'" or "," or "[" or "]"
		ret = ret.replaceAll("[:()',\\[\\]]", "");

		ret = ret.replaceAll("\\s", "_");

		if (!ret.startsWith("@")) {
			ret = "@" + ret;
		}

		return ret;
	}

	private static void writeCsv(List<WorkshopItem> workshopItems, String fileName) throws WriteException {
		try (Writer writer = new FileWriter(MOD_LOCATION + fileName + ".csv")) {
			final StatefulBeanToCsv<WorkshopItem> toCsv = new StatefulBeanToCsvBuilder<WorkshopItem>(writer)
					.withApplyQuotesToAll(false)
					.build();
			toCsv.write(workshopItems);
		} catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
			throw new WriteException("Failed to write csv " + fileName, e);
		}
	}
}
