package io.moneyinthesky.dashboard.data.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatedNodeStatus {
    private Integer nodeCount = 0;
    private List<NodeStatus> nodesForVersion = new ArrayList<>();
    private Map<String, DependencyStatus> unhealthyDependencies = new HashMap<>();

    public void incrementNodeCount() {
        nodeCount++;
    }

    public Integer getNodeCount() {
        return nodeCount;
    }

    public void addToUnhealthyDependencies(DependencyStatus dependencyStatus) {
        unhealthyDependencies.put(dependencyStatus.getName(), dependencyStatus);
    }

    public Map<String, DependencyStatus> getUnhealthyDependencies() {
        return unhealthyDependencies;
    }

    public void addToNodesForVersion(NodeStatus nodeStatus) {
        nodesForVersion.add(nodeStatus);
    }

    public List<NodeStatus> getNodesForVersion() {
        return nodesForVersion;
    }
}
