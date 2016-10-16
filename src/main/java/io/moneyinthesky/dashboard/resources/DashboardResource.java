package io.moneyinthesky.dashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.data.dashboard.DashboardData;
import io.moneyinthesky.dashboard.service.DashboardDataService;
import org.slf4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

	private static Logger logger = getLogger(DashboardResource.class);
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

        return objectMapper.writeValueAsString(dashboardData);
    }
}
