package io.moneyinthesky.dashboard.nodediscovery.fleet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.moneyinthesky.dashboard.core.aspects.LogExecutionTime;
import io.moneyinthesky.dashboard.core.dao.SettingsDao;
import io.moneyinthesky.dashboard.core.data.settings.Settings;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.mashape.unirest.http.Unirest.get;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.slf4j.LoggerFactory.getLogger;

class FleetRestClient {

    private static final Logger logger = getLogger(FleetRestClient.class);

    private ObjectMapper objectMapper;
    private SettingsDao settingsDao;

    private Supplier<List<Map<String, String>>> fleetResponseSupplier;

    @Inject
    public FleetRestClient(ObjectMapper objectMapper, SettingsDao settingsDao) throws IOException {
        this.objectMapper = objectMapper;
        this.settingsDao = settingsDao;

        fleetResponseSupplier = memoizeWithExpiration(this::generateFleetHosts, 10, MINUTES);
    }

    List<Map<String, String>> getFleetHosts() {
        return fleetResponseSupplier.get();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> generateFleetHosts() {
        List<Map<String, String>> fleetHosts = newArrayList();
        Map<String, Map<String, Object>> fleetResponses = new HashMap<>();

        Settings settings;
        try {
            settings = settingsDao.readSettings();
        } catch (IOException e) {
            logger.error("Unable to read settings JSON", e);
            return newArrayList();
        }

        Set<String> fleetRestApiUrls = newHashSet((List<String>) settings.getPlugins().get("fleet").get("restApiUrls"));
        getAndPopulateFleetResponses(fleetResponses, fleetRestApiUrls);

        fleetResponses.values()
                .forEach(fleetResponse -> fleetHosts.addAll((List<Map<String, String>>) fleetResponse.get("hosts")));

        return fleetHosts;
    }

    @SuppressWarnings("unchecked")
    @LogExecutionTime(value="Time taken to query Fleet: %f")
    protected void getAndPopulateFleetResponses(Map<String, Map<String, Object>> fleetResponses, Set<String> fleetRestApiUrls) {
        logger.info("Retrieving hosts from Fleet on " + fleetRestApiUrls);
        fleetRestApiUrls.forEach(fleetRestUrl -> {
            HttpResponse<String> fleetResponse = null;
            try {
                fleetResponse = get(fleetRestUrl).asString();
                fleetResponses.put(fleetRestUrl, objectMapper.readValue(fleetResponse.getBody(), Map.class));

            } catch (UnirestException e) {
                logger.error("Unable to retrieve response from Fleet on " + fleetRestUrl, e);
            } catch (IOException e) {
                logger.error("Unable to parse JSON response from Fleet - URL: " + fleetRestUrl, e);
            }
        });
    }
}
