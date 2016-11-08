package io.moneyinthesky.dashboard.core.resources;

import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.moneyinthesky.dashboard.core.app.Application;
import io.moneyinthesky.dashboard.core.app.ApplicationConfiguration;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.assertj.core.api.Assertions.assertThat;

public class SettingsResourceTest {

    @ClassRule
    public static final DropwizardAppRule<ApplicationConfiguration> RULE =
            new DropwizardAppRule<>(Application.class, resourceFilePath("configuration.yml"));

    @Test
    public void testGetSettings() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");

        Response response = client.target(
                String.format("http://localhost:%d/api/settings", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
    }

}
