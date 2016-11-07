package io.moneyinthesky.dashboard.core.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.mashape.unirest.http.Unirest;
import io.moneyinthesky.dashboard.statuspopulation.DefaultNodeStatusPopulation;
import io.moneyinthesky.dashboard.statuspopulation.NodeStatusPopulation;

public class DashboardModule extends AbstractModule {

	protected void configure() {
		Unirest.setTimeouts(2000, 5000);
		bind(ObjectMapper.class).toInstance(new ObjectMapper());
		bind(NodeStatusPopulation.class).to(DefaultNodeStatusPopulation.class).asEagerSingleton();
	}
}
