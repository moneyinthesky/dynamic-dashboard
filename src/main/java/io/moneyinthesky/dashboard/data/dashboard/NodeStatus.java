package io.moneyinthesky.dashboard.data.dashboard;

import java.util.List;

public class NodeStatus {
	private String url;
	private String statusUrl;
	private String infoUrl;
	private String version;
	private boolean up;
	private String errorMessage;
	private List<DependencyStatus> dependencyStatus;

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

	public List<DependencyStatus> getDependencyStatus() {
		return dependencyStatus;
	}

	public void setDependencyStatus(List<DependencyStatus> dependencyStatus) {
		this.dependencyStatus = dependencyStatus;
	}
}