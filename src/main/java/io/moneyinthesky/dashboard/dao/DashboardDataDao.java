package io.moneyinthesky.dashboard.dao;

import com.google.inject.Inject;
import io.moneyinthesky.dashboard.data.DashboardData;
import io.moneyinthesky.dashboard.data.Settings;

import java.io.IOException;

public class DashboardDataDao {

	private SettingsDao settingsDao;

	@Inject
	public DashboardDataDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}

	public DashboardData generateDashboardData() throws IOException {
		Settings settings = settingsDao.readSettings();

		return null;
	}

}
