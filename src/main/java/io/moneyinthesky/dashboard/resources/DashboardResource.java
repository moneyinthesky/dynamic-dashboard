package io.moneyinthesky.dashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.data.dashboard.DashboardData;
import io.moneyinthesky.dashboard.service.DashboardDataService;
import org.slf4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;
import static java.time.format.FormatStyle.LONG;
import static org.slf4j.LoggerFactory.getLogger;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

	private static Logger logger = getLogger(DashboardResource.class);

	private static final ZoneId TIMEZONE = ZoneId.of("Europe/London");
	private DashboardDataService dashboardDataService;

	private ObjectMapper objectMapper;

	@Inject
	public DashboardResource(DashboardDataService dashboardDataService, ObjectMapper objectMapper) {
		this.dashboardDataService = dashboardDataService;
		this.objectMapper = objectMapper;
	}

	@GET
    public String getData() throws IOException {;
		DashboardData dashboardData = dashboardDataService.getDashboardData();

		if (dashboardData != null) {
			logger.info("Dashboard generated time: " + dashboardData.getTimeGenerated());
		} else {
			logger.info("No data yet");
		}

        Map<String, Object> data = objectMapper.readValue(Resources.toString(getResource("data.json"), UTF_8), Map.class);
        return objectMapper.writeValueAsString(timestamp(data));
    }

    private Map<String, Object> timestamp(Map<String, Object> data) {
        ZonedDateTime nowWithTimeZone = ZonedDateTime.of(now(), TIMEZONE);
        data.put("timeGenerated", nowWithTimeZone.format(ofLocalizedDateTime(LONG)));
        return data;
    }
}
