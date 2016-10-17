package io.moneyinthesky.dashboard.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import io.moneyinthesky.dashboard.data.dashboard.*;
import io.moneyinthesky.dashboard.data.settings.DataCenter;
import io.moneyinthesky.dashboard.data.settings.Environment;
import io.moneyinthesky.dashboard.data.settings.Settings;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;
import io.moneyinthesky.dashboard.nodediscovery.UrlPatternMethod;
import org.slf4j.Logger;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static com.mashape.unirest.http.Unirest.get;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static java.time.format.FormatStyle.LONG;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

public class DashboardDataDao {

	private static final Logger logger = getLogger(DashboardDataDao.class);
	private static final ZoneId TIMEZONE = ZoneId.of("Europe/London");
	private static Function<String, NodeDiscoveryMethod> discoveryMethodMapper;

	private SettingsDao settingsDao;
	private UrlPatternMethod urlPatternMethod;
	private ObjectMapper objectMapper;
	private ForkJoinPool forkJoinPool = new ForkJoinPool(32);

	@Inject
	public DashboardDataDao(SettingsDao settingsDao, UrlPatternMethod urlPatternMethod, ObjectMapper objectMapper) throws IOException {
		this.settingsDao = settingsDao;
		this.urlPatternMethod = urlPatternMethod;
		this.objectMapper = objectMapper;

		discoveryMethodMapper = (method) -> {
			if (method.equals("urlPattern")) {
				return urlPatternMethod;
			}
			return null;
		};
	}

	public DashboardData populateDashboardData() throws IOException {
		DashboardData data = new DashboardData();
		Settings settings = settingsDao.readSettings();

		List<DataCenterStatus> dataCenters = settings.getDataCenters()
				.stream()
				.map(dataCenter -> generateDataCenterStatus(dataCenter, settings))
				.collect(toList());

		data.setDataCenters(dataCenters);
		aggregateNodeData(data);
		data.setTimeGenerated(getTimestamp());
		return data;
	}

	private DataCenterStatus generateDataCenterStatus(DataCenter dataCenter, Settings settings) {
		DataCenterStatus dataCenterStatus = new DataCenterStatus();
		dataCenterStatus.setName(dataCenter.getName());
		dataCenterStatus.setEnvironments(getEnvironmentNames(dataCenter));

		List<ApplicationStatus> applicationStatusList = settings.getApplications()
				.stream()
				.map(application -> generateApplicationStatus(application, dataCenter.getEnvironments()))
				.collect(toList());

		dataCenterStatus.setApplications(applicationStatusList);
		return dataCenterStatus;
	}

	private ApplicationStatus generateApplicationStatus(String application, List<Environment> environments) {
		ApplicationStatus applicationStatus = new ApplicationStatus();
		applicationStatus.setName(application);

		Map<String, EnvironmentStatus> environmentStatusMap = environments
				.parallelStream()
				.map(environment -> generateEnvironmentStatusForApplication(environment, application))
				.collect(toMap(environmentStatus -> environmentStatus.getName(), environmentStatus -> environmentStatus));

		applicationStatus.setEnvironmentStatusMap(environmentStatusMap);
		return applicationStatus;
	}

	private EnvironmentStatus generateEnvironmentStatusForApplication(Environment environment, String application) {
		EnvironmentStatus environmentStatus = new EnvironmentStatus();

		NodeDiscoveryMethod discoveryMethod = discoveryMethodMapper.apply(environment.getNodeDiscoveryMethod());

		Map<String, String> applicationConfig = environment.getApplicationConfig().get(application);
		List<String> urls = applicationConfig != null ? discoveryMethod.generateNodeUrls(applicationConfig) : newArrayList();

		environmentStatus.setName(environment.getName());
		environmentStatus.setNodeStatusList(generateNodeStatusList(urls));
		return environmentStatus;
	}

	private List<NodeStatus> generateNodeStatusList(List<String> urls) {
		try {
			return forkJoinPool.submit(() ->
                urls
                    .parallelStream()
                    .map((url) -> {
                        NodeStatus nodeStatus = new NodeStatus();
                        nodeStatus.setUrl(url);

                        try {
                            HttpResponse<String> response = get(url).asString();

                            if (response.getStatus() == 200) {
                                nodeStatus.up(true);

                                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
                                nodeStatus.setVersion((String) responseBody.get("version"));

                            } else {
                                nodeStatus.up(false);
                                nodeStatus.setErrorMessage("Status Code: " + response.getStatus());
                            }
                            return nodeStatus;

                        } catch (Exception e) {
                            logger.error(format("Error while calling %s", url), e);
                            nodeStatus.up(false);
                            nodeStatus.setErrorMessage("Unknown error");
                            return nodeStatus;
                        }
                    })
                    .collect(toList())
            ).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void aggregateNodeData(DashboardData data) {
		data.getDataCenters()
				.forEach(dataCenterStatus -> dataCenterStatus.getApplications()
						.forEach(applicationStatus -> {
							applicationStatus.getEnvironmentStatusMap().entrySet()
									.forEach(environmentStatusEntry -> addAggregatedEnvironmentNodeData(environmentStatusEntry.getValue()));
						}));
	}

	private void addAggregatedEnvironmentNodeData(EnvironmentStatus environmentStatus) {
		Map<String, AggregatedNodeStatus> aggregatedNodeStatusMap = new HashMap<>();

		for(NodeStatus nodeStatus : environmentStatus.getNodeStatusList()) {
			if(nodeStatus.getVersion() != null) {
				AggregatedNodeStatus aggregatedNodeStatus = aggregatedNodeStatusMap.get(nodeStatus.getVersion());
				if(aggregatedNodeStatus == null) {
					aggregatedNodeStatus = new AggregatedNodeStatus();
					aggregatedNodeStatusMap.put(nodeStatus.getVersion(), aggregatedNodeStatus);
				}

				if(nodeStatus.isUp())
					aggregatedNodeStatus.incrementNodeCount();

			} else {
				if(!nodeStatus.isUp())
					environmentStatus.incrementNodesDown();
			}
		}

		environmentStatus.setVersionToNodeStatusMap(aggregatedNodeStatusMap);
	}

	private List<String> getEnvironmentNames(DataCenter dataCenter) {
		return dataCenter.getEnvironments()
				.stream()
				.map((environment) -> environment.getName())
				.collect(toList());
	}

	private String getTimestamp() {
		ZonedDateTime nowWithTimeZone = ZonedDateTime.of(now(), TIMEZONE);
		return nowWithTimeZone.format(ofLocalizedDateTime(LONG));
	}
}
