package io.moneyinthesky.dashboard.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;

public class DashboardModule extends AbstractModule {

	protected void configure() {
		bind(ObjectMapper.class).toInstance(new ObjectMapper());
	}
}
