package io.moneyinthesky.dashboard.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.moneyinthesky.dashboard.data.dashboard.DependencyStatus;
import io.moneyinthesky.dashboard.data.dashboard.NodeStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import static com.google.common.collect.Sets.newHashSet;
import static com.mashape.unirest.http.Unirest.get;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

class NodeStatusRetrieval {

    private static final Logger logger = getLogger(NodeStatusRetrieval.class);
    private static final Set IGNORED_DEPENDENCY_KEYS = newHashSet("version", "environment", "buildTimestamp");

    private ObjectMapper objectMapper;
    private ForkJoinPool forkJoinPool = new ForkJoinPool(32);

    @Inject
    public NodeStatusRetrieval(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    void populateNodeStatus(List<NodeStatus> nodeStatusList) {
        long start = currentTimeMillis();

        try {
            forkJoinPool.submit(() ->
                    nodeStatusList
                            .parallelStream()
                            .forEach(this::getAndProcessNodeStatus)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error thrown from node status population thread pool", e);
        }

        logger.info(String.format("Time taken to retrieve status of %d nodes: %f", nodeStatusList.size(), (currentTimeMillis() - start) / 1000d));
    }

    private void getAndProcessNodeStatus(NodeStatus nodeStatus) {
        try {

            HttpResponse<String> statusResponse = get(nodeStatus.getStatusUrl()).asString();
            if (isNodeUp(statusResponse)) {
                nodeStatus.up(true);
                getAndProcessInfoPage(nodeStatus);

            } else {
                nodeStatus.up(false);
                nodeStatus.setErrorMessage("HTTP Status Code: " + statusResponse.getStatus() + " from status page");
                logger.info("Status Code: {} from {}", statusResponse.getStatus(), nodeStatus.getStatusUrl());
            }

        } catch (UnirestException e) {
            nodeStatus.up(false);
            nodeStatus.setErrorMessage("Error calling node: " + e.getMessage());
            logger.error("Error calling {} - {}", nodeStatus.getUrl(), e.getMessage());
        }
    }

    private void getAndProcessInfoPage(NodeStatus nodeStatus) throws UnirestException {
        HttpResponse<String> infoResponse = get(nodeStatus.getInfoUrl()).asString();
        if(is2XXResponse(infoResponse)) {
            populateNodeStatusBasedOnInfoResponse(infoResponse, nodeStatus);

        } else {
            nodeStatus.setVersion("???");
            nodeStatus.setInfoPageUnavailable(true);
            nodeStatus.setErrorMessage("HTTP status code: " + infoResponse.getStatus() + " from info page");
            logger.info("Status code: {} from {}", infoResponse.getStatus(), nodeStatus.getInfoUrl());
        }
    }

    @SuppressWarnings("unchecked")
    private void populateNodeStatusBasedOnInfoResponse(HttpResponse<String> infoResponse, NodeStatus nodeStatus) {
        try {
            Map<String, Object> responseBody = objectMapper.readValue(infoResponse.getBody(), Map.class);
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
                            (Boolean) dependencyInfo.get("running") ? "UP" : "DOWN");
                    })
                .collect(toList());

        if(dependencyStatusList != null)
            nodeStatus.setDependencyStatus(dependencyStatusList);
    }

    private boolean isNodeUp(HttpResponse<String> statusResponse) {
        return (is2XXResponse(statusResponse)) && statusResponse.getBody().equals("OK");
    }

    private boolean is2XXResponse(HttpResponse<String> infoResponse) {
        return infoResponse.getStatus() >= 200 && infoResponse.getStatus() < 300;
    }
}
