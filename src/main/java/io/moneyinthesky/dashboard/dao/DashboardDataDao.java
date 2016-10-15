package io.moneyinthesky.dashboard.dao;

import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.moneyinthesky.dashboard.data.DashboardData;
import io.moneyinthesky.dashboard.data.Settings;
import io.moneyinthesky.dashboard.patterns.ExplodableString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDataDao {

	private SettingsDao settingsDao;

	@Inject
	public DashboardDataDao(SettingsDao settingsDao) {
		this.settingsDao = settingsDao;
	}

	public DashboardData generateDashboardData() throws IOException {
		Settings settings = settingsDao.readSettings();

		List<String> urls = new ArrayList<>();
		settings.getDataCenters().stream()
				.forEach((dataCenter) -> dataCenter.getEnvironments().stream()
				.forEach((environment) -> environment.getApplicationConfig().entrySet().stream()
				.forEach((applicationConfig) -> ExplodableString.explode(applicationConfig.getValue().get("urlPattern")).stream()
				.forEach((url) -> urls.add(url)))));

		System.out.println(urls);
		urls.stream().forEach((url) -> {
			try {
				HttpResponse<String> response = Unirest.get(url).asString();
				System.out.println(response.getBody());
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		});

		return null;
	}
}
