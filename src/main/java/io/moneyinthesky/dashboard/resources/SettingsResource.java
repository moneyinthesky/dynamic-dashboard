package io.moneyinthesky.dashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.dao.SettingsDao;
import io.moneyinthesky.dashboard.data.settings.Settings;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/settings")
@Produces(MediaType.APPLICATION_JSON)
public class SettingsResource {

	private SettingsDao settingsDao;
	private ObjectMapper objectMapper;

	@Inject
	public SettingsResource(SettingsDao settingsDao, ObjectMapper objectMapper) {
		this.settingsDao = settingsDao;
		this.objectMapper = objectMapper;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSettings() throws IOException {
		Settings settings = settingsDao.readSettings();
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void setSettings(Settings settings) throws IOException {
		settingsDao.writeSettings(settings);
	}

}
