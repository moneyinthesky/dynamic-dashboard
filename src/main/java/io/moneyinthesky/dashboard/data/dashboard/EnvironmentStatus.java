package io.moneyinthesky.dashboard.data.dashboard;

import java.util.List;

public class EnvironmentStatus {
	private String name;
	private List<NodeStatus> nodeStatusList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NodeStatus> getNodeStatusList() {
		return nodeStatusList;
	}

	public void setNodeStatusList(List<NodeStatus> nodeStatusList) {
		this.nodeStatusList = nodeStatusList;
	}
}