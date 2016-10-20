package io.moneyinthesky.dashboard.nodediscovery.fleet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.moneyinthesky.dashboard.dao.SettingsDao;
import io.moneyinthesky.dashboard.data.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedRateSchedule;
import static com.mashape.unirest.http.Unirest.get;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MINUTES;

public class FleetRestClient extends AbstractScheduledService {

    private static final Logger logger = LoggerFactory.getLogger(FleetRestClient.class);

    private ObjectMapper objectMapper;
    private SettingsDao settingsDao;
    private Map<String, Map<String, Object>> cachedFleetResponse;

    @Inject
    public FleetRestClient(ObjectMapper objectMapper, SettingsDao settingsDao) throws IOException {
        this.objectMapper = objectMapper;
        this.settingsDao = settingsDao;

        cachedFleetResponse = new HashMap<>();

        logger.info("Starting Fleet Rest Client");
        super.startAsync();
    }

    public Map<String, Object> getFleetResponse(String fleetRestUrl) {
        return cachedFleetResponse.get(fleetRestUrl);
    }

    @Override
    protected void runOneIteration() throws Exception {
        Settings settings = settingsDao.readSettings();
        Set<String> fleetRestApiUrls = newHashSet((List<String>) settings.getPlugins().get("fleet").get("restApiUrls"));

        logger.info("Retrieving hosts from Fleet on " + fleetRestApiUrls);
        long start = currentTimeMillis();
        fleetRestApiUrls.forEach(fleetRestUrl -> {
            HttpResponse<String> fleetResponse = null;
            try {
                fleetResponse = get(fleetRestUrl).asString();
                cachedFleetResponse.put(fleetRestUrl, objectMapper.readValue(fleetResponse.getBody(), Map.class));

            } catch (UnirestException e) {
                logger.error("Unable to retrieve response from Fleet on " + fleetRestUrl, e);
            } catch (IOException e) {
                logger.error("Unable to parse JSON response from Fleet - URL: " + fleetRestUrl, e);
            }
        });
		logger.info("Time take to query Fleet {}", (currentTimeMillis() - start)/1000d);
    }

    @Override
    protected Scheduler scheduler() {
        return newFixedRateSchedule(0, 10, MINUTES);
    }
}
