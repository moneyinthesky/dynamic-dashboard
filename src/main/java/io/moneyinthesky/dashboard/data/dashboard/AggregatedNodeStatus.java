package io.moneyinthesky.dashboard.data.dashboard;

public class AggregatedNodeStatus {
    private Integer nodeCount = 0;

    public void incrementNodeCount() {
        nodeCount++;
    }

    public Integer getNodeCount() {
        return nodeCount;
    }
}
