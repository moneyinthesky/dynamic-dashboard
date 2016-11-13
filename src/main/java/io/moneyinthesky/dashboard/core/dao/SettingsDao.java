package io.moneyinthesky.dashboard.core.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.core.app.guice.annotations.SettingsFile;
import io.moneyinthesky.dashboard.core.data.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.google.common.io.Files.createParentDirs;

public class SettingsDao {

	private static final Logger logger = LoggerFactory.getLogger(SettingsDao.class);
	private ObjectMapper objectMapper;
	private String settingsFile;

	@Inject
	public SettingsDao(ObjectMapper objectMapper, @SettingsFile String settingsFile) {
		this.objectMapper = objectMapper;
		this.settingsFile = settingsFile;
	}

	public Settings readSettings() throws IOException {
		Settings settings;
		try {
			settings = objectMapper.readValue(new File(settingsFile), Settings.class);

		} catch(FileNotFoundException e) {
			logger.warn("A settings.json file could not be found - creating one");
			settings = new Settings();
			createParentDirs(new File(settingsFile));
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(settingsFile), settings);
		}

		return settings;
	}

	public void writeSettings(Settings settings) throws IOException {
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(settingsFile), settings);
	}
}
