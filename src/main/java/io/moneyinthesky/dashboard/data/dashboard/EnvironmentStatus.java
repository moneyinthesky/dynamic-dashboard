package io.moneyinthesky.dashboard.data.dashboard;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class EnvironmentStatus {
	private String name;
	private List<NodeStatus> nodeStatusList;
	private Map<String, AggregatedNodeStatus> versionToNodeStatusMap;
	private List<NodeStatus> unhealthyNodes = newArrayList();
	private Integer nodesDown = 0;

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

	public Map<String, AggregatedNodeStatus> getVersionToNodeStatusMap() {
		return versionToNodeStatusMap;
	}

	public void setVersionToNodeStatusMap(Map<String, AggregatedNodeStatus> versionToNodeStatusMap) {
		this.versionToNodeStatusMap = versionToNodeStatusMap;
	}

	public Integer getNodesDown() {
		return nodesDown;
	}

	public void incrementNodesDown() {
		nodesDown++;
	}

	public void addToUnhealthyNodes(NodeStatus nodeStatus) {
		unhealthyNodes.add(nodeStatus);
	}

	public List<NodeStatus> getUnhealthyNodes() {
		return unhealthyNodes;
	}
}