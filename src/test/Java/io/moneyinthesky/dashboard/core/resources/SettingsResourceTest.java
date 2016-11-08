package io.moneyinthesky.dashboard.core.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.moneyinthesky.dashboard.core.app.dropwizard.Application;
import io.moneyinthesky.dashboard.core.app.dropwizard.ApplicationConfiguration;
import io.moneyinthesky.dashboard.core.data.settings.Settings;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import java.io.IOException;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.assertj.core.api.Assertions.assertThat;

public class SettingsResourceTest {

    @ClassRule
    public static final DropwizardAppRule<ApplicationConfiguration> RULE =
            new DropwizardAppRule<>(Application.class, resourceFilePath("configuration.yml"));

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetSettings() throws IOException {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");

        Response response = client.target(
                String.format("http://localhost:%d/api/settings", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        Settings actualSettings = objectMapper.readValue(response.readEntity(String.class), Settings.class);
        assertThat(actualSettings.getTitle()).isEqualTo("Dynamic Dashboard");
    }

}
