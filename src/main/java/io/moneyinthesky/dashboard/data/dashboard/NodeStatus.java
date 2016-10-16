package io.moneyinthesky.dashboard.data.dashboard;

public class NodeStatus {
	private String url;
	private String version;
	private Boolean up;
	private String errorMessage;

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

	public Boolean getUp() {
		return up;
	}

	public void up(Boolean up) {
		this.up = up;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}