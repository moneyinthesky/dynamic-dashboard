package io.moneyinthesky.dashboard.core.dao;

import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.moneyinthesky.dashboard.core.app.guice.ForkJoinPoolSize;
import io.moneyinthesky.dashboard.core.aspects.LogExecutionTime;
import io.moneyinthesky.dashboard.core.data.dashboard.NodeStatus;
import io.moneyinthesky.dashboard.statuspopulation.NodeStatusPopulation;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import static com.mashape.unirest.http.Unirest.get;
import static org.slf4j.LoggerFactory.getLogger;

class NodeStatusRetrieval {

    private static final Logger logger = getLogger(NodeStatusRetrieval.class);

    private NodeStatusPopulation nodeStatusPopulation;
    private ForkJoinPool forkJoinPool;

    @Inject
    public NodeStatusRetrieval(NodeStatusPopulation nodeStatusPopulation, @ForkJoinPoolSize int forkJoinPoolSize) {
        this.nodeStatusPopulation = nodeStatusPopulation;
        this.forkJoinPool = new ForkJoinPool(forkJoinPoolSize);
    }

    @LogExecutionTime
    void populateNodeStatus(List<NodeStatus> nodeStatusList) {
        logger.info("Populating {} nodes", nodeStatusList.size());

        try {
            forkJoinPool.submit(() ->
                    nodeStatusList
                            .parallelStream()
                            .forEach(this::getAndProcessNodeStatus)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error thrown from node status population thread pool", e);
        }
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
