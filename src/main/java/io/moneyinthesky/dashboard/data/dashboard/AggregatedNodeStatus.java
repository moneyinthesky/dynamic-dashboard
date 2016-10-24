package io.moneyinthesky.dashboard.data.dashboard;

import java.util.HashMap;
import java.util.Map;

public class AggregatedNodeStatus {
    private Integer nodeCount = 0;
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
}
