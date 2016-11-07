package io.moneyinthesky.dashboard.core.data.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class EnvironmentStatus {
	private String name;
	private List<NodeStatus> nodeStatusList = newArrayList();
	private Map<String, AggregatedNodeStatus> versionToNodeStatusMap = new HashMap<>();
	private List<NodeStatus> unhealthyNodes = newArrayList();
	private Integer nodesDown = 0;
	private List<NodeStatus> unknownVersionNodes = newArrayList();

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

	public List<NodeStatus> getUnknownVersionNodes() {
		return unknownVersionNodes;
	}

	public void addToUnknownVersionNodes(NodeStatus nodeStatus) {
		unknownVersionNodes.add(nodeStatus);
	}
}