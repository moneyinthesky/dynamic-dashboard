package io.moneyinthesky.dashboard.core.dao;

import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.moneyinthesky.dashboard.core.data.dashboard.NodeStatus;
import io.moneyinthesky.dashboard.statuspopulation.NodeStatusPopulation;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import static com.mashape.unirest.http.Unirest.get;
import static java.lang.System.currentTimeMillis;
import static org.slf4j.LoggerFactory.getLogger;

class NodeStatusRetrieval {

    private static final Logger logger = getLogger(NodeStatusRetrieval.class);

    private NodeStatusPopulation nodeStatusPopulation;
    private ForkJoinPool forkJoinPool = new ForkJoinPool(32);

    @Inject
    public NodeStatusRetrieval(NodeStatusPopulation nodeStatusPopulation) {
        this.nodeStatusPopulation = nodeStatusPopulation;
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

            boolean nodeIsUp = nodeStatusPopulation.populateNodeStatus(nodeStatus, statusResponse);
            if(nodeIsUp) {
                getAndProcessInfoPage(nodeStatus);
            }

        } catch (UnirestException e) {
            nodeStatus.up(false);
            nodeStatus.setErrorMessage("Error calling node: " + e.getMessage());
            logger.error("Error calling {} - {}", nodeStatus.getUrl(), e.getMessage());
        }
    }

    private void getAndProcessInfoPage(NodeStatus nodeStatus) throws UnirestException {
        HttpResponse<String> infoResponse = get(nodeStatus.getInfoUrl()).asString();
        nodeStatusPopulation.populateNodeInfo(nodeStatus, infoResponse);
    }
}
