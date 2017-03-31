package io.moneyinthesky.dashboard.statuspopulation;

import com.mashape.unirest.http.HttpResponse;
import io.moneyinthesky.dashboard.core.data.dashboard.EnvironmentStatus;
import io.moneyinthesky.dashboard.core.data.dashboard.NodeStatus;

public interface NodeStatusPopulation {

    boolean populateNodeStatus(NodeStatus nodeStatus, HttpResponse<String> response);

    boolean populateNodeInfo(NodeStatus nodeStatus, HttpResponse<String> response);

    void addAggregatedEnvironmentNodeStatusData(EnvironmentStatus environmentStatus);
}
