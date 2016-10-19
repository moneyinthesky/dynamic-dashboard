package io.moneyinthesky.dashboard.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.moneyinthesky.dashboard.data.dashboard.NodeStatus;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static io.moneyinthesky.dashboard.patterns.ExplodableString.explode;
import static java.util.stream.Collectors.toList;

@RunWith(MockitoJUnitRunner.class)
public class NodeStatusRetrievalTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NodeStatusRetrieval nodeStatusRetrieval;

    @Test
    @Ignore
    public void tester() {
        String format = "http://h1wap[01-20]-v01.dcm.stg2.ovp.bskyb.com";
        List<String> nodeStatusUrls = newArrayList(explode(format));

        nodeStatusUrls.addAll(explode("http://dcm-app-v02-[01-10][a-b].u3euw1.api.bskyb.com"));
        nodeStatusUrls.addAll(explode("http://h1wap[01-20]-v01.dcm.stg1.ovp.bskyb.com"));
        nodeStatusUrls.addAll(explode("http://dcm-app-v03-[01-10][a-b].s1euw1.api.bskyb.com"));


        List<NodeStatus> nodeStatusList = nodeStatusUrls.stream().map(nodeStatusUrl -> {
            NodeStatus nodeStatus = new NodeStatus();
            nodeStatus.setStatusUrl(nodeStatusUrl + "/dcm/private/status");
            return nodeStatus;
        }).collect(toList());

        nodeStatusRetrieval.populateNodeStatusAlternate(nodeStatusList);
    }
}
