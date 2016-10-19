package io.moneyinthesky.dashboard.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.moneyinthesky.dashboard.data.dashboard.NodeStatus;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import static com.mashape.unirest.http.Unirest.get;
import static org.slf4j.LoggerFactory.getLogger;

public class NodeStatusRetrieval {

    private static final Logger logger = getLogger(NodeStatusRetrieval.class);

    private ObjectMapper objectMapper;

    @Inject
    public NodeStatusRetrieval(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void populateNodeStatus(List<NodeStatus> nodeStatusList) {
        long start = System.currentTimeMillis();
        try {
            ForkJoinPool forkJoinPool = new ForkJoinPool(32);
            forkJoinPool.submit(() ->
                    nodeStatusList
                            .parallelStream()
                            .forEach(nodeStatus -> {
                                        try {

                                            HttpResponse<String> statusResponse = get(nodeStatus.getStatusUrl()).asString();

                                            if (statusResponse.getStatus() == 200 && statusResponse.getBody().equals("OK")) {
                                                nodeStatus.up(true);

                                                HttpResponse<String> infoResponse = get(nodeStatus.getInfoUrl()).asString();
                                                if(infoResponse.getStatus() == 200) {
                                                    Map<String, Object> responseBody = null;
                                                    try {
                                                        responseBody = objectMapper.readValue(infoResponse.getBody(), Map.class);
                                                        nodeStatus.setVersion((String) responseBody.get("version"));
                                                    } catch (IOException e) {
                                                        nodeStatus.setVersion("unknown");
                                                        nodeStatus.setErrorMessage("Unable to deserialize info response: " + nodeStatus.getInfoUrl());
                                                        logger.info("Unable to deserialize info response: " + nodeStatus.getInfoUrl());
                                                    }

                                                } else {
                                                    nodeStatus.setVersion("unknown");
                                                    nodeStatus.setErrorMessage("Info page not responding: " + nodeStatus.getInfoUrl());
                                                    logger.info("Info page not responding: " + nodeStatus.getInfoUrl());
                                                }
                                            } else {
                                                nodeStatus.up(false);
                                                nodeStatus.setErrorMessage("Status Code: " + statusResponse.getStatus() + " from " + nodeStatus.getStatusUrl());
                                                logger.info("Status Code: " + statusResponse.getStatus() + " from " + nodeStatus.getStatusUrl());
                                            }

                                        } catch (UnirestException e) {
                                            nodeStatus.up(false);
                                            nodeStatus.setErrorMessage("Error calling " + nodeStatus.getUrl() + ": " + e.getMessage());
                                            logger.error("Error calling " + nodeStatus.getUrl(), e);
                                        }
                                    }
                            )).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        logger.info(String.format("Time taken to retrieve status of %d nodes: %f", nodeStatusList.size(), (System.currentTimeMillis() - start) / 1000d));
    }
}
