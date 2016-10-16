package io.moneyinthesky.dashboard.data.dashboard;

import java.util.Map;

public class ApplicationStatus {
	private String name;
	private Map<String, EnvironmentStatus> environmentStatuses;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, EnvironmentStatus> getEnvironmentStatuses() {
		return environmentStatuses;
	}

	public void setEnvironmentStatuses(Map<String, EnvironmentStatus> environmentStatuses) {
		this.environmentStatuses = environmentStatuses;
	}
}