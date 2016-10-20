package io.moneyinthesky.dashboard.nodediscovery.fleet;

import com.google.inject.Inject;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

public class FleetDiscoveryMethod implements NodeDiscoveryMethod {

    private static final Logger logger = getLogger(FleetDiscoveryMethod.class);
    private FleetRestClient fleetRestClient;

    @Inject
    public FleetDiscoveryMethod(FleetRestClient fleetRestClient) {
        this.fleetRestClient = fleetRestClient;
    }

    @Override
    public List<String> generateNodeUrls(Map<String, String> configuration) {
        long start = System.currentTimeMillis();

        String fleetRestUrl = configuration.get("fleetRestUrl");
        String appId = configuration.get("appId");
        String envId = configuration.get("envId");
        String dataCenterId = configuration.get("dataCenterId");
        String roleId = configuration.get("roleId");

        Map<String, Object> fleetJson = fleetRestClient.getFleetResponse(fleetRestUrl);

        List<Map<String, String>> hosts = (List<Map<String, String>>) fleetJson.get("hosts");
        List<Map<String, String>> filteredHosts = hosts
                .stream()
                .filter(host ->
                        (host.get("app").equals(appId) &&
                                host.get("env").equals(envId) &&
                                host.get("datacenter").equals(dataCenterId) &&
                                host.get("role").equals(roleId)))
                .collect(toList());

        logger.info(String.format("Time taken to query Fleet: %f", (System.currentTimeMillis() - start) / 1000d));
        return filteredHosts.stream().map(host -> "http://" + host.get("fqdn")).collect(toList());
    }
}
