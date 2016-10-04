package io.moneyinthesky.dashboard;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.moneyinthesky.dashboard.resources.StatusResource;

public class Application extends io.dropwizard.Application<ApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    public void run(ApplicationConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().setUrlPattern("/api/*");
        environment.jersey().register(new StatusResource());
    }

    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }
}
