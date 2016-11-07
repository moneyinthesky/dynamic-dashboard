package io.moneyinthesky.dashboard.core.data.settings;

import java.util.Map;

public class Environment {
	private String name;
	private String nodeDiscoveryMethod;
	private Map<String, Map<String, String>> applicationConfig;

	public String getName() {
		return name;
	}

	public String getNodeDiscoveryMethod() {
		return nodeDiscoveryMethod;
	}

	public Map<String, Map<String, String>> getApplicationConfig() {
		return applicationConfig;
	}
}
