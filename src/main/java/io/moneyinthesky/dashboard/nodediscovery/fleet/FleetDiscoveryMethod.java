package io.moneyinthesky.dashboard.nodediscovery.fleet;

import com.google.inject.Inject;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class FleetDiscoveryMethod implements NodeDiscoveryMethod {

    private FleetRestClient fleetRestClient;

    @Inject
    public FleetDiscoveryMethod(FleetRestClient fleetRestClient) {
        this.fleetRestClient = fleetRestClient;
    }

    @Override
    public List<String> generateNodeUrls(Map<String, String> configuration) {
        List<String> nodeUrls = newArrayList();

        String appId = configuration.get("appId");
        String envId = configuration.get("envId");
        String dataCenterId = configuration.get("dataCenterId");
        String roleId = configuration.get("roleId");

        List<Map<String, String>> hosts = fleetRestClient.getFleetHosts();

        if(hosts != null) {
            List<Map<String, String>> filteredHosts = hosts
                    .stream()
                    .filter(host ->
                            (host.get("app").equals(appId) &&
                                    host.get("env").equals(envId) &&
                                    host.get("datacenter").equals(dataCenterId) &&
                                    host.get("role").equals(roleId)))
                    .collect(toList());
            nodeUrls = filteredHosts.stream().map(host -> "http://" + host.get("fqdn")).collect(toList());
        }

        return nodeUrls;
    }
}
