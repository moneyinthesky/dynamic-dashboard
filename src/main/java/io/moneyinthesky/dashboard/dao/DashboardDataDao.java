package io.moneyinthesky.dashboard.dao;

import com.google.inject.Inject;
import io.moneyinthesky.dashboard.data.DashboardData;
import io.moneyinthesky.dashboard.data.Settings;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;
import io.moneyinthesky.dashboard.nodediscovery.UrlPatternMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DashboardDataDao {

    private static Function<String, NodeDiscoveryMethod> discoveryMethodMapper;
	private SettingsDao settingsDao;
    private UrlPatternMethod urlPatternMethod;

	@Inject
	public DashboardDataDao(SettingsDao settingsDao, UrlPatternMethod urlPatternMethod) {
		this.settingsDao = settingsDao;
        this.urlPatternMethod = urlPatternMethod;

        discoveryMethodMapper = (method) -> {
            if(method.equals("urlPattern")) {
                return urlPatternMethod;
            }
            return null;
        };
    }

	public DashboardData generateDashboardData() throws IOException {
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
                                //TODO

                            });

                    Map<String, DashboardData.EnvironmentStatus> environments = new HashMap<>();
                    dataCenter.getEnvironments()
                            .forEach((environment) -> {
                                DashboardData.EnvironmentStatus environmentStatus = new DashboardData.EnvironmentStatus();

                                NodeDiscoveryMethod discoveryMethod = discoveryMethodMapper.apply(environment.getNodeDiscoveryMethod());
                                //TODO
                            });
                        });

		return data;
	}

    private List<String> getEnvironmentNames(Settings.DataCenter dataCenter) {
        //TODO
        return null;
    }
}
