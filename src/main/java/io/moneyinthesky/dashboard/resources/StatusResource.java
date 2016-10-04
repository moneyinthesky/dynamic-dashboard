package io.moneyinthesky.dashboard.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/status")
@Produces(MediaType.TEXT_PLAIN)
public class StatusResource {

    @GET
    public String getStatus() {
        return "OK";
    }

}
