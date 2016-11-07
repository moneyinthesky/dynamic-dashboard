package io.moneyinthesky.dashboard.core.data.dashboard;

import java.util.Map;

public class ApplicationStatus {
	private String name;
	private Map<String, EnvironmentStatus> environmentStatusMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, EnvironmentStatus> getEnvironmentStatusMap() {
		return environmentStatusMap;
	}

	public void setEnvironmentStatusMap(Map<String, EnvironmentStatus> environmentStatusMap) {
		this.environmentStatusMap = environmentStatusMap;
	}
}