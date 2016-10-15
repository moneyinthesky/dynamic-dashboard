package io.moneyinthesky.dashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.moneyinthesky.dashboard.data.PersistedSettings;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;

@Path("/settings")
@Produces(MediaType.APPLICATION_JSON)
public class SettingsResource {

	private static final String PERSISTED_SETTINGS_JSON = "persisted/settings.json";
	private ObjectMapper objectMapper = new ObjectMapper();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getSettings() throws IOException {
		PersistedSettings settings = objectMapper.readValue(new File(PERSISTED_SETTINGS_JSON), PersistedSettings.class);
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void setSettings(PersistedSettings settings) throws IOException {
		objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(PERSISTED_SETTINGS_JSON), settings);
	}

}
