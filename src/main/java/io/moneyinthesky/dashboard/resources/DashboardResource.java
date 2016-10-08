package io.moneyinthesky.dashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.moneyinthesky.dashboard.data.PersistedSettings;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.Map;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

	private static final String PERSISTED_SETTINGS_JSON = "persisted/settings.json";
	private ObjectMapper objectMapper = new ObjectMapper();

    @GET
    public String getData() throws IOException {
        Map<String, Object> jsonObject = objectMapper.readValue(Resources.toString(getResource("data.json"), UTF_8), Map.class);

        ZonedDateTime nowWithTimeZone = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/London"));
        jsonObject.put("timeGenerated", nowWithTimeZone.format(ofLocalizedDateTime(FormatStyle.LONG)));

        return objectMapper.writeValueAsString(jsonObject);
    }

    @GET
    @Path("/settings")
    public PersistedSettings getSettings() throws IOException {
        return objectMapper.readValue(new File(PERSISTED_SETTINGS_JSON), PersistedSettings.class);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/settings")
    public void setSettings(PersistedSettings settings) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(PERSISTED_SETTINGS_JSON), settings);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/settingsJson")
    public String getSettingsJson() throws IOException {
        PersistedSettings settings = objectMapper.readValue(new File(PERSISTED_SETTINGS_JSON), PersistedSettings.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(settings);
    }
}
