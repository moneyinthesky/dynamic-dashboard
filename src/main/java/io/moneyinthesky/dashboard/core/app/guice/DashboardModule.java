package io.moneyinthesky.dashboard.core.app.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.mashape.unirest.http.Unirest;
import io.moneyinthesky.dashboard.core.app.dropwizard.configuration.ApplicationConfiguration;
import io.moneyinthesky.dashboard.core.app.guice.annotations.AwsResponseFile;
import io.moneyinthesky.dashboard.core.app.guice.annotations.ForkJoinPoolSize;
import io.moneyinthesky.dashboard.core.app.guice.annotations.SettingsFile;
import io.moneyinthesky.dashboard.core.aspects.LogExecutionTime;
import io.moneyinthesky.dashboard.core.aspects.LogExecutionTimeInterceptor;
import io.moneyinthesky.dashboard.core.service.DashboardDataService;
import io.moneyinthesky.dashboard.nodediscovery.NodeDiscoveryMethod;
import io.moneyinthesky.dashboard.nodediscovery.aws.AwsDiscoveryMethod;
import io.moneyinthesky.dashboard.nodediscovery.fleet.FleetDiscoveryMethod;
import io.moneyinthesky.dashboard.nodediscovery.urlpattern.UrlPatternDiscoveryMethod;
import io.moneyinthesky.dashboard.statuspopulation.DefaultNodeStatusPopulation;
import io.moneyinthesky.dashboard.statuspopulation.NodeStatusPopulation;

import static com.google.inject.matcher.Matchers.*;
import static com.google.inject.multibindings.MapBinder.newMapBinder;

public class DashboardModule extends AbstractModule {

	private ApplicationConfiguration configuration;

	public DashboardModule(ApplicationConfiguration configuration) {
		this.configuration = configuration;
	}

	protected void configure() {
		bind(String.class).annotatedWith(SettingsFile.class).toInstance(configuration.getSettingsFile());
		bind(String.class).annotatedWith(AwsResponseFile.class).toInstance(configuration.getAwsResponseFile());
		bind(Integer.class).annotatedWith(ForkJoinPoolSize.class)
				.toInstance(configuration.getNodeStatusRetrievalConfiguration().getForkJoinPoolSize());

		MapBinder<String, NodeDiscoveryMethod> nodeDiscoveryMethodMap = newMapBinder(binder(), String.class, NodeDiscoveryMethod.class);
		nodeDiscoveryMethodMap.addBinding("urlPattern").to(UrlPatternDiscoveryMethod.class);
		nodeDiscoveryMethodMap.addBinding("fleet").to(FleetDiscoveryMethod.class);
		nodeDiscoveryMethodMap.addBinding("aws").to(AwsDiscoveryMethod.class);

		bindInterceptor(any(), annotatedWith(LogExecutionTime.class), new LogExecutionTimeInterceptor());

		bind(NodeStatusPopulation.class).to(DefaultNodeStatusPopulation.class).asEagerSingleton();
		bind(DashboardDataService.class).asEagerSingleton();

		bind(ObjectMapper.class).toInstance(new ObjectMapper());

		Unirest.setTimeouts(configuration.getConnectivityConfiguration().getConnectionTimeout(),
				configuration.getConnectivityConfiguration().getSocketTimeout());
	}
}
