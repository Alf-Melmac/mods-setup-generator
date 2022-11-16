package de.webalf.model;

/**
 * @author Alf
 * @since 29.11.2021
 */
public class WorkshopItem {
	public WorkshopItem(long steamid, String name, String directory) {
		this.steamid = steamid;
		this.name = name;
		this.directory = directory;
	}

	@SuppressWarnings("unused") //Used by CSV Reader
	public WorkshopItem() {
	}

	private long steamid;

	private String name;

	private String directory;

	public long getSteamid() {
		return steamid;
	}

	public String getDirectory() {
		return directory;
	}
}
