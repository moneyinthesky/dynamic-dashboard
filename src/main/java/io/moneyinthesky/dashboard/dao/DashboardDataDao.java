package io.moneyinthesky.dashboard.dao;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.data.dashboard.*;
import io.moneyinthesky.dashboard.data.settings.DataCenter;
import io.moneyinthesky.dashboard.data.settings.Environment;
import io.moneyinthesky.dashboard.data.settings.Settings;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;
import io.moneyinthesky.dashboard.nodediscovery.aws.AwsDiscoveryMethod;
import io.moneyinthesky.dashboard.nodediscovery.fleet.FleetDiscoveryMethod;
import io.moneyinthesky.dashboard.nodediscovery.urlpattern.UrlPatternDiscoveryMethod;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static java.time.format.FormatStyle.LONG;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class DashboardDataDao {

	private static final ZoneId TIMEZONE = ZoneId.of("Europe/London");
	private static Map<String, NodeDiscoveryMethod> discoveryMethodMap;

	private SettingsDao settingsDao;
	private NodeStatusRetrieval nodeStatusRetrieval;

	private List<NodeStatus> nodeStatusList;
	private Settings settings;

	@Inject
	public DashboardDataDao(SettingsDao settingsDao, NodeStatusRetrieval nodeStatusRetrieval,
							UrlPatternDiscoveryMethod urlPatternDiscoveryMethod, FleetDiscoveryMethod fleetDiscoveryMethod,
							AwsDiscoveryMethod awsDiscoveryMethod) throws IOException {
		this.settingsDao = settingsDao;
		this.nodeStatusRetrieval = nodeStatusRetrieval;

		discoveryMethodMap = ImmutableMap.of(
				"urlPattern", urlPatternDiscoveryMethod,
				"fleet", fleetDiscoveryMethod,
				"aws", awsDiscoveryMethod);
	}

	public DashboardData populateDashboardData() throws IOException {
		DashboardData data = new DashboardData();
		settings = settingsDao.readSettings();
		nodeStatusList = new ArrayList<>();

		if(settings.getDataCenters() != null) {
			List<DataCenterStatus> dataCenters = settings.getDataCenters()
					.stream()
					.map(dataCenter -> generateDataCenterStatus(dataCenter, settings))
					.collect(toList());
			data.setDataCenters(dataCenters);
			nodeStatusRetrieval.populateNodeStatus(nodeStatusList);

			aggregateNodeData(data);
			data.setTimeGenerated(getTimestamp());
		}
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
				.stream()
				.map(environment -> generateEnvironmentStatusForApplication(environment, application))
				.collect(toMap(EnvironmentStatus::getName, environmentStatus -> environmentStatus));

		applicationStatus.setEnvironmentStatusMap(environmentStatusMap);
		return applicationStatus;
	}

	private EnvironmentStatus generateEnvironmentStatusForApplication(Environment environment, String application) {
		EnvironmentStatus environmentStatus = new EnvironmentStatus();
		environmentStatus.setName(environment.getName());

		if(environment.getNodeDiscoveryMethod() != null && environment.getApplicationConfig() != null ) {
			NodeDiscoveryMethod discoveryMethod = discoveryMethodMap.get(environment.getNodeDiscoveryMethod());

			Map<String, String> applicationConfig = environment.getApplicationConfig().get(application);
			List<String> urls = applicationConfig != null ? discoveryMethod.generateNodeUrls(applicationConfig) : newArrayList();

			environmentStatus.setNodeStatusList(urls
					.stream()
					.map(url -> {
						NodeStatus unpopulatedNodeStatus = new NodeStatus();
						unpopulatedNodeStatus.setUrl(url);
						unpopulatedNodeStatus.setStatusUrl(url + settings.getApplicationConfig().get(application).get("statusUri"));
						unpopulatedNodeStatus.setInfoUrl(url + settings.getApplicationConfig().get(application).get("infoUri"));
						unpopulatedNodeStatus.setIdentifier(url.replace("http://", ""));
						nodeStatusList.add(unpopulatedNodeStatus);
						return unpopulatedNodeStatus;
					})
					.collect(toList()));
		}

		return environmentStatus;
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
			if(!nodeStatus.isInfoPageUnavailable() && nodeStatus.isUp()) {
				AggregatedNodeStatus aggregatedNodeStatus = aggregatedNodeStatusMap.get(nodeStatus.getVersion());
				if(aggregatedNodeStatus == null) {
					aggregatedNodeStatus = new AggregatedNodeStatus();
					aggregatedNodeStatusMap.put(nodeStatus.getVersion(), aggregatedNodeStatus);
				}

				if(nodeStatus.isUp())
					aggregatedNodeStatus.incrementNodeCount();

				for(DependencyStatus dependencyStatus : nodeStatus.getDependencyStatus()) {
					if(dependencyStatus.getStatus().equals("DOWN")) {
						aggregatedNodeStatus.addToUnhealthyDependencies(dependencyStatus);
						nodeStatus.addToDownDependencies(dependencyStatus);
					} else if(dependencyStatus.getStatus().equals("DISABLED")){
						aggregatedNodeStatus.addToDisabledDependencies(dependencyStatus);
						nodeStatus.addToDisabledDependencies(dependencyStatus);
					}
				}

				aggregatedNodeStatus.addToNodesForVersion(nodeStatus);

			} else if(nodeStatus.isUp()) {
				environmentStatus.addToUnknownVersionNodes(nodeStatus);

			} else {
				environmentStatus.incrementNodesDown();
				environmentStatus.addToUnhealthyNodes(nodeStatus);
			}
		}

		environmentStatus.setVersionToNodeStatusMap(aggregatedNodeStatusMap);
	}

	private List<String> getEnvironmentNames(DataCenter dataCenter) {
		return dataCenter.getEnvironments()
				.stream()
				.map(Environment::getName)
				.collect(toList());
	}

	private String getTimestamp() {
		ZonedDateTime nowWithTimeZone = ZonedDateTime.of(now(), TIMEZONE);
		return nowWithTimeZone.format(ofLocalizedDateTime(LONG));
	}
}
