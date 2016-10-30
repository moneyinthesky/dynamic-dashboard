package io.moneyinthesky.dashboard.data.dashboard;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class NodeStatus {
	private String url;
	private String statusUrl;
	private String infoUrl;
	private String identifier;
	private String version;
	private boolean up;
	private String errorMessage;
	private List<DependencyStatus> dependencyStatus = newArrayList();
	private List<DependencyStatus> downDependencies = newArrayList();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isUp() {
		return up;
	}

	public void up(boolean up) {
		this.up = up;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatusUrl() {
		return statusUrl;
	}

	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	public String getInfoUrl() {
		return infoUrl;
	}

	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public List<DependencyStatus> getDependencyStatus() {
		return dependencyStatus;
	}

	public void setDependencyStatus(List<DependencyStatus> dependencyStatus) {
		this.dependencyStatus = dependencyStatus;
	}

	public void addToDownDependencies(DependencyStatus dependencyStatus) {
		downDependencies.add(dependencyStatus);
	}

	public List<DependencyStatus> getDownDependencies() {
		return downDependencies;
	}
}