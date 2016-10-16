package io.moneyinthesky.dashboard.data.dashboard;

import java.util.List;

public class DashboardData {

	private String timeGenerated;
	private List<DataCenterStatus> dataCenters;

	public String getTimeGenerated() {
		return timeGenerated;
	}

	public List<DataCenterStatus> getDataCenters() {
		return dataCenters;
	}

	public void setTimeGenerated(String timeGenerated) {
		this.timeGenerated = timeGenerated;
	}

	public void setDataCenters(List<DataCenterStatus> dataCenters) {
		this.dataCenters = dataCenters;
	}
}
