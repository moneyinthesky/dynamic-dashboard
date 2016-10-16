package io.moneyinthesky.dashboard.data.dashboard;

import java.util.List;

public class EnvironmentStatus {
	private List<NodeStatus> nodeStatuses;

	public List<NodeStatus> getNodeStatuses() {
		return nodeStatuses;
	}

	public void setNodeStatuses(List<NodeStatus> nodeStatuses) {
		this.nodeStatuses = nodeStatuses;
	}
}