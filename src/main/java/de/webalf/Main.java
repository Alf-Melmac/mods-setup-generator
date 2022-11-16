package de.webalf;

import de.webalf.service.CSVParser;
import de.webalf.service.HTMLParser;

import java.io.File;

import static de.webalf.Constants.MOD_LOCATION;

/**
 * @author Alf
 * @since 29.11.2021
 */
public class Main {
	public static void main(String[] args) {
		if (args == null || args.length != 1) {
			System.err.println("One argument needed");
			System.exit(400);
		}

		final String arg = args[0];

		if ("refresh".equals(arg)) {
			for (File file : new File(MOD_LOCATION).listFiles()) {
				final String fileName = file.getName();
				if (file.isFile() && fileName.endsWith(".html")) {
					HTMLParser.parse(fileName);
				}
			}
			for (File file : new File(MOD_LOCATION).listFiles()) {
				final String fileName = file.getName();
				if (file.isFile() && fileName.endsWith(".csv")) {
					CSVParser.parse(fileName);
				}
			}
		} else {
			if (arg.endsWith(".csv")) {
				CSVParser.parse(arg);
			} else if (arg.endsWith(".html")) {
				HTMLParser.parse(arg);
			} else {
				System.err.println("Can only parse csv or html files");
				System.exit(400);
			}
		}
	}
}
