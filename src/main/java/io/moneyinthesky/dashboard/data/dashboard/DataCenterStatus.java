package io.moneyinthesky.dashboard.data.dashboard;

import java.util.List;

public class DataCenterStatus {

	private String name;
	private List<String> environments;
	private List<ApplicationStatus> applications;

	public String getName() {
		return name;
	}

	public List<String> getEnvironments() {
		return environments;
	}

	public List<ApplicationStatus> getApplications() {
		return applications;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEnvironments(List<String> environments) {
		this.environments = environments;
	}

	public void setApplications(List<ApplicationStatus> applications) {
		this.applications = applications;
	}

}
