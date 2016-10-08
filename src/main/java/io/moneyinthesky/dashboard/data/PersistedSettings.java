package io.moneyinthesky.dashboard.data;

import java.util.List;
import java.util.Map;

public class PersistedSettings {
    private String title;
    private List<String> applications;
    private Map<String, DataCenter> dataCenters;
	private String primaryDataCenter;

    public String getTitle() {
        return title;
    }

    public List<String> getApplications() {
        return applications;
    }

    public Map<String, DataCenter> getDataCenters() {
        return dataCenters;
    }

    public String getPrimaryDataCenter() {
		return primaryDataCenter;
	}

	public static class DataCenter {
        List<String> environments;

        public List<String> getEnvironments() {
            return environments;
        }
    }
}
