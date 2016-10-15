package io.moneyinthesky.dashboard.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.data.Settings;

import java.io.File;
import java.io.IOException;

public class SettingsDao {

	private static final String PERSISTED_SETTINGS_JSON = "persisted/settings.json";
	private ObjectMapper objectMapper;

	@Inject
	public SettingsDao(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public Settings readSettings() throws IOException {
		return objectMapper.readValue(new File(PERSISTED_SETTINGS_JSON), Settings.class);
	}

	public void writeSettings(Settings settings) throws IOException {
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(PERSISTED_SETTINGS_JSON), settings);
	}
}
