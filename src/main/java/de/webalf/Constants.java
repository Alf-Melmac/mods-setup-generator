package de.webalf;

import java.util.Set;

/**
 * @author Alf
 * @since 15.11.2022
 */
public class Constants {
	private Constants() {
	}

	public static final String MOD_LOCATION = "src/main/resources/mods/";

	public static final String MOD_PREFIX = "SET MOD%s=";
	public static final String MODNAME_PREFIX = "SET MODNAME%s=";

	public static final Set<Long> SKIP = Set.of(2430905589L /*TacControl*/, 708250744L /*ACEX*/);
}
