package io.moneyinthesky.dashboard.core.data.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        addToListInOrder(nodesForVersion, nodeStatus);
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

    private List<NodeStatus> addToListInOrder(List<NodeStatus> list, NodeStatus nodeStatus) {
        //TODO make this less ugly
        if(list.isEmpty())
            list.add(nodeStatus);
        else {
            int initialSize = list.size();
            for(int i = 0; i < initialSize; i++) {
                if(nodeStatus.getUrl().compareTo(list.get(i).getUrl()) < 0) {
                    list.add(i, nodeStatus);
                    return list;
                }
            }
            list.add(initialSize, nodeStatus);
        }
        return list;
    }
}
