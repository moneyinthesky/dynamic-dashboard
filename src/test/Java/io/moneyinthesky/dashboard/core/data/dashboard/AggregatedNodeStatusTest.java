package io.moneyinthesky.dashboard.core.data.dashboard;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class AggregatedNodeStatusTest {

    @Test
    public void nodesForVersionIsOrderedByUrl() {
        NodeStatus nodeStatusA = buildNodeStatusWithUrlSetTo("A");
        NodeStatus nodeStatusB = buildNodeStatusWithUrlSetTo("B");
        NodeStatus nodeStatusC = buildNodeStatusWithUrlSetTo("C");

        AggregatedNodeStatus aggregatedNodeStatus = new AggregatedNodeStatus();
        aggregatedNodeStatus.addToNodesForVersion(nodeStatusC);
        aggregatedNodeStatus.addToNodesForVersion(nodeStatusA);
        aggregatedNodeStatus.addToNodesForVersion(nodeStatusB);

        assertEquals(asList(nodeStatusA, nodeStatusB, nodeStatusC),
                aggregatedNodeStatus.getNodesForVersion());
    }

    private NodeStatus buildNodeStatusWithUrlSetTo(String url) {
        NodeStatus nodeStatus = new NodeStatus();
        nodeStatus.setUrl(url);
        return nodeStatus;
    }

}