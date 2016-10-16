package io.moneyinthesky.dashboard.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import io.moneyinthesky.dashboard.data.DashboardData;
import io.moneyinthesky.dashboard.data.Settings;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;
import io.moneyinthesky.dashboard.nodediscovery.UrlPatternMethod;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedRateSchedule;
import static com.mashape.unirest.http.Unirest.get;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

public class DashboardDataDao extends AbstractScheduledService {

	private static Logger logger = getLogger(DashboardDataDao.class);
	private static Function<String, NodeDiscoveryMethod> discoveryMethodMapper;

	private SettingsDao settingsDao;
	private UrlPatternMethod urlPatternMethod;
	private ObjectMapper objectMapper;

	private DashboardData cachedDashboardData;

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

		logger.info("Starting up dashboard data dao");
		super.startAsync();
	}

	public DashboardData getDashboardData() {
		return cachedDashboardData;
	}

	public boolean getStatus() {
		return super.isRunning();
	}

	private void populateDashboardData() throws IOException {
		logger.info("Still Populating dashboard data");
		DashboardData data = new DashboardData();
		Settings settings = settingsDao.readSettings();

		List<DashboardData.DataCenterStatus> dataCenters = new ArrayList<>();

		settings.getDataCenters()
				.forEach((dataCenter) -> {
					DashboardData.DataCenterStatus dataCenterStatus = new DashboardData.DataCenterStatus();
					dataCenterStatus.setName(dataCenter.getName());
					dataCenterStatus.setEnvironments(getEnvironmentNames(dataCenter));

					List<DashboardData.ApplicationStatus> applicationStatuses = new ArrayList<>();
					settings.getApplications()
							.forEach((application) -> {
								DashboardData.ApplicationStatus applicationStatus = new DashboardData.ApplicationStatus();
								applicationStatus.setName(application);

								Map<String, DashboardData.EnvironmentStatus> environmentStatuses = new HashMap<>();
								dataCenter.getEnvironments()
										.forEach((environment) -> {
											DashboardData.EnvironmentStatus environmentStatus = new DashboardData.EnvironmentStatus();

											NodeDiscoveryMethod discoveryMethod = discoveryMethodMapper.apply(environment.getNodeDiscoveryMethod());
											List<String> urls = discoveryMethod.generateNodeUrls(environment.getApplicationConfig().get(application));
											List<DashboardData.NodeStatus> nodeStatuses = generateNodeStatuses(urls);

											environmentStatus.setNodeStatuses(nodeStatuses);
											environmentStatuses.put(environment.getName(), environmentStatus);
										});

								applicationStatus.setEnvironmentStatuses(environmentStatuses);
								applicationStatuses.add(applicationStatus);
							});
					dataCenterStatus.setApplications(applicationStatuses);
					dataCenters.add(dataCenterStatus);
				});

		data.setDataCenters(dataCenters);
		cachedDashboardData = data;
	}

	private List<DashboardData.NodeStatus> generateNodeStatuses(List<String> urls) {
		return urls
				.parallelStream()
				.map((url) -> {
					DashboardData.NodeStatus nodeStatus = new DashboardData.NodeStatus();
					nodeStatus.setUrl(url);

					try {
						HttpResponse<String> response = get(url).asString();

						if(response.getStatus() == 200) {
							nodeStatus.setUp(true);

							Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
							nodeStatus.setVersion((String) responseBody.get("version"));

						} else {
							nodeStatus.setUp(false);
							nodeStatus.setErrorMessage("Status Code: " + response.getStatus());
						}
						return nodeStatus;

					} catch (Exception e) {
						logger.error(format("Error while calling %s", url), e);
						nodeStatus.setUp(false);
						nodeStatus.setErrorMessage("Unknown error");
						return nodeStatus;
					}
				})
				.collect(toList());
	}

	private List<String> getEnvironmentNames(Settings.DataCenter dataCenter) {
		return dataCenter.getEnvironments()
				.stream()
				.map((environment) -> environment.getName())
				.collect(toList());
	}

	@Override
	protected void runOneIteration() throws Exception {
		//TODO Add timing logging info
		populateDashboardData();
	}

	@Override
	protected Scheduler scheduler() {
		return newFixedRateSchedule(0, 60, TimeUnit.SECONDS);
	}
}
