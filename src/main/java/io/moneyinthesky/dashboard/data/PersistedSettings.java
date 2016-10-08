package io.moneyinthesky.dashboard.data;

import java.util.List;

public class PersistedSettings {
    private String title;
    private List<String> applications;
    private List<String> dataCenters;
	private String primaryDataCenter;

    public String getTitle() {
        return title;
    }

    public List<String> getApplications() {
        return applications;
    }

	public List<String> getDataCenters() {
		return dataCenters;
	}

	public String getPrimaryDataCenter() {
		return primaryDataCenter;
	}
}
