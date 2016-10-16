package io.moneyinthesky.dashboard.data.settings;

import java.util.List;

public class DataCenter {
	private String name;
	private List<Environment> environments;

	public String getName() {
		return name;
	}

	public List<Environment> getEnvironments() {
		return environments;
	}
}