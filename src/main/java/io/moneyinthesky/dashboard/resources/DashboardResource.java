package io.moneyinthesky.dashboard.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.FormatStyle;
import java.util.Map;

import static com.google.common.io.Resources.getResource;
import static java.time.format.DateTimeFormatter.ofLocalizedDateTime;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @GET
    public String getData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        URL url = getResource("data.json");
        Map<String, Object> jsonObject = objectMapper.readValue(Resources.toString(url, Charsets.UTF_8), Map.class);

        ZonedDateTime nowWithTimeZone = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/London"));
        jsonObject.put("timeGenerated", nowWithTimeZone.format(ofLocalizedDateTime(FormatStyle.LONG)));

        return objectMapper.writeValueAsString(jsonObject);
    }
}
