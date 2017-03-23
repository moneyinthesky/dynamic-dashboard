package io.moneyinthesky.dashboard.core.data.dashboard;

import java.util.*;

import static java.util.Comparator.comparing;

public class AggregatedNodeStatus {
    private Integer nodeCount = 0;
    private List<NodeStatus> nodesForVersion = new ArrayList<>();
    private Map<String, DependencyStatus> unhealthyDependencies = new HashMap<>();
    private Map<String, DependencyStatus> disabledDependencies = new HashMap<>();

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
        nodesForVersion.sort(comparing(NodeStatus::getUrl));
    }

    public List<NodeStatus> getNodesForVersion() {
        return nodesForVersion;
    }

    public void addToDisabledDependencies(DependencyStatus dependencyStatus) {
        disabledDependencies.put(dependencyStatus.getName(), dependencyStatus);
    }

    public Map<String, DependencyStatus> getDisabledDependencies() {
        return disabledDependencies;
    }
}
