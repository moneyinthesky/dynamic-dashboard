package io.moneyinthesky.dashboard.nodediscovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

public class FleetDiscoveryMethod implements NodeDiscoveryMethod {

    private static final Logger logger = getLogger(FleetDiscoveryMethod.class);
    private ObjectMapper objectMapper;

    @Inject
    public FleetDiscoveryMethod(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> generateNodeUrls(Map<String, String> configuration) {
        long start = System.currentTimeMillis();

        List<Map<String, String>> filteredHosts = newArrayList();

        String fleetRestUrl = configuration.get("fleetRestUrl");
        String appId = configuration.get("appId");
        String envId = configuration.get("envId");
        String dataCenterId = configuration.get("dataCenterId");
        String roleId = configuration.get("roleId");

        try {
            HttpResponse<String> fleetResponse = Unirest.get(fleetRestUrl).asString();
            Map<String, Object> fleetJson = objectMapper.readValue(fleetResponse.getBody(), Map.class);

            List<Map<String, String>> hosts = (List<Map<String, String>>) fleetJson.get("hosts");
            filteredHosts = hosts
                    .stream()
                    .filter(host ->
                            (host.get("app").equals(appId) &&
                            host.get("env").equals(envId) &&
                            host.get("datacenter").equals(dataCenterId) &&
                            host.get("role").equals(roleId)))
                    .collect(toList());

        } catch (UnirestException e) {
            logger.error("Unable to reach fleet rest URL: " + fleetRestUrl, e);
        } catch (IOException e) {
            logger.error("Unable to deserialize response from Fleet: " + fleetRestUrl, e);
        }

        logger.info(String.format("Time taken to query Fleet: %f", (System.currentTimeMillis() - start) / 1000d));
        return filteredHosts.stream().map(host -> host.get("fqdn")).collect(toList());
    }
}
