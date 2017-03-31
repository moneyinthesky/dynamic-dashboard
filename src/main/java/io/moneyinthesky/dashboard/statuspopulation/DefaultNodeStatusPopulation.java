package io.moneyinthesky.dashboard.statuspopulation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import io.moneyinthesky.dashboard.core.data.dashboard.AggregatedNodeStatus;
import io.moneyinthesky.dashboard.core.data.dashboard.DependencyStatus;
import io.moneyinthesky.dashboard.core.data.dashboard.EnvironmentStatus;
import io.moneyinthesky.dashboard.core.data.dashboard.NodeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;

public class DefaultNodeStatusPopulation implements NodeStatusPopulation {

    private static final Logger logger = LoggerFactory.getLogger(DefaultNodeStatusPopulation.class);
    private static final Set IGNORED_DEPENDENCY_KEYS = newHashSet("version", "environment", "buildTimestamp", "dependencies");

    private ObjectMapper objectMapper;

    @Inject
    public DefaultNodeStatusPopulation(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean populateNodeStatus(NodeStatus nodeStatus, HttpResponse<String> response) {
        if (isNodeUp(response)) {
            nodeStatus.up(true);
            return true;

        } else {
            nodeStatus.up(false);
            nodeStatus.setErrorMessage("HTTP Status Code: " + response.getStatus() + " from status page");
            logger.info("Status Code: {} from {}", response.getStatus(), nodeStatus.getStatusUrl());
            return false;
        }
    }

    @Override
    public boolean populateNodeInfo(NodeStatus nodeStatus, HttpResponse<String> response) {
        if(is2XXResponse(response)) {
            populateNodeStatusBasedOnInfoResponse(response, nodeStatus);
            return true;

        } else {
            nodeStatus.setVersion("???");
            nodeStatus.setInfoPageUnavailable(true);
            nodeStatus.setErrorMessage("HTTP status code: " + response.getStatus() + " from info page");
            logger.info("Status code: {} from {}", response.getStatus(), nodeStatus.getInfoUrl());
            return false;
        }
    }

    @Override
    public void addAggregatedEnvironmentNodeStatusData(EnvironmentStatus environmentStatus) {
        Map<String, AggregatedNodeStatus> aggregatedNodeStatusMap = new HashMap<>();


        environmentStatus.getNodeStatusList()
                .forEach(nodeStatus -> {
                    if(!nodeStatus.isInfoPageUnavailable() && nodeStatus.isUp()) {
                        populateAggregatedNodeStatusForVersion(nodeStatus, aggregatedNodeStatusMap);

                    } else if(nodeStatus.isUp()) {
                        environmentStatus.addToUnknownVersionNodes(nodeStatus);

                    } else {
                        environmentStatus.incrementNodesDown();
                        environmentStatus.addToUnhealthyNodes(nodeStatus);
                    }
                });

        environmentStatus.setVersionToNodeStatusMap(aggregatedNodeStatusMap);
    }

    private void populateAggregatedNodeStatusForVersion(NodeStatus nodeStatus, Map<String, AggregatedNodeStatus> aggregatedNodeStatusMap) {
        AggregatedNodeStatus aggregatedNodeStatus = aggregatedNodeStatusMap
                .computeIfAbsent(nodeStatus.getVersion(), key -> new AggregatedNodeStatus());

        aggregatedNodeStatus.incrementNodeCount();

        for(DependencyStatus dependencyStatus : nodeStatus.getDependencyStatus()) {
            if(dependencyStatus.getStatus().equals("DOWN")) {
                aggregatedNodeStatus.addToUnhealthyDependencies(dependencyStatus);
                nodeStatus.addToDownDependencies(dependencyStatus);
            } else if(dependencyStatus.getStatus().equals("DISABLED")){
                aggregatedNodeStatus.addToDisabledDependencies(dependencyStatus);
                nodeStatus.addToDisabledDependencies(dependencyStatus);
            }
        }

        aggregatedNodeStatus.addToNodesForVersion(nodeStatus);
    }

    private void populateNodeStatusBasedOnInfoResponse(HttpResponse<String> infoResponse, NodeStatus nodeStatus) {
        try {
            Map<String, Object> responseBody = objectMapper.readValue(infoResponse.getBody(), new TypeReference<Map<String, Object>>(){});
            nodeStatus.setVersion((String) responseBody.get("version"));

            populateDependencyStatusInfo(nodeStatus, responseBody);

        } catch (IOException e) {
            nodeStatus.setVersion("???");
            nodeStatus.setInfoPageUnavailable(true);
            nodeStatus.setErrorMessage("Unable to deserialize info page response");
            logger.info("Unable to deserialize info page response");
        }
    }

    @SuppressWarnings("unchecked")
    private void populateDependencyStatusInfo(NodeStatus nodeStatus, Map<String, Object> responseBody) {
        List<DependencyStatus> dependencyStatusList = responseBody.entrySet().stream()
                .filter(entry -> !IGNORED_DEPENDENCY_KEYS.contains(entry.getKey()))
                .map(entry -> {
                    Map<String, Object> dependencyInfo = (Map<String, Object>) entry.getValue();
                    return new DependencyStatus(
                            entry.getKey(),
                            (String) dependencyInfo.get("endpoint"),
                            getDependencyStatus(dependencyInfo));
                })
                .collect(toList());

        // Support alternate info page structure
        if(responseBody.get("dependencies") != null) {
            Map<String, Object> alternateDependencyMap = (Map<String, Object>) responseBody.get("dependencies");
            alternateDependencyMap
                    .forEach((key, value) ->
                            dependencyStatusList.add(new DependencyStatus(key, null, getDependencyStatus((Map<String, Object>) value))));
        }

        if(dependencyStatusList != null)
            nodeStatus.setDependencyStatus(dependencyStatusList);
    }

    private String getDependencyStatus(Map<String, Object> dependencyInfo) {
        return (String) dependencyInfo.getOrDefault("status", ((Boolean) dependencyInfo.get("running")) ? "UP" : "DOWN");
    }

    private boolean isNodeUp(HttpResponse<String> statusResponse) {
        return (is2XXResponse(statusResponse)) && statusResponse.getBody().equals("OK");
    }

    private boolean is2XXResponse(HttpResponse<String> infoResponse) {
        return infoResponse.getStatus() >= 200 && infoResponse.getStatus() < 300;
    }
}
