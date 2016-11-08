package io.moneyinthesky.dashboard.core.app.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.mashape.unirest.http.Unirest;
import io.moneyinthesky.dashboard.core.app.dropwizard.ApplicationConfiguration;
import io.moneyinthesky.dashboard.core.aspects.LogExecutionTime;
import io.moneyinthesky.dashboard.core.aspects.LogExecutionTimeInterceptor;
import io.moneyinthesky.dashboard.core.service.DashboardDataService;
import io.moneyinthesky.dashboard.statuspopulation.DefaultNodeStatusPopulation;
import io.moneyinthesky.dashboard.statuspopulation.NodeStatusPopulation;

import static com.google.inject.matcher.Matchers.*;

public class DashboardModule extends AbstractModule {

	private ApplicationConfiguration configuration;

	public DashboardModule(ApplicationConfiguration configuration) {
		this.configuration = configuration;
	}

	protected void configure() {
		bind(String.class).annotatedWith(SettingsFile.class).toInstance(configuration.getSettingsFile());
		bind(String.class).annotatedWith(AwsResponseFile.class).toInstance(configuration.getAwsResponseFile());

		bindInterceptor(any(), annotatedWith(LogExecutionTime.class), new LogExecutionTimeInterceptor());

		bind(NodeStatusPopulation.class).to(DefaultNodeStatusPopulation.class).asEagerSingleton();
		bind(DashboardDataService.class).asEagerSingleton();

		bind(ObjectMapper.class).toInstance(new ObjectMapper());

		Unirest.setTimeouts(2000, 5000);
	}
}
