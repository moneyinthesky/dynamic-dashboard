package io.moneyinthesky.dashboard.resources;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.net.URL;

import static com.google.common.io.Resources.getResource;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @GET
    public String getData() throws IOException {
        URL url = getResource("data.json");
        return Resources.toString(url, Charsets.UTF_8);
    }
}
