package io.moneyinthesky.dashboard.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.mashape.unirest.http.Unirest;

public class DashboardModule extends AbstractModule {

	protected void configure() {
		Unirest.setTimeouts(2000, 5000);
		bind(ObjectMapper.class).toInstance(new ObjectMapper());
	}
}
