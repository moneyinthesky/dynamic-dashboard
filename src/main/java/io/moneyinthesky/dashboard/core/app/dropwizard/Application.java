package io.moneyinthesky.dashboard.core.app.dropwizard;

import com.google.inject.Injector;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.moneyinthesky.dashboard.core.app.dropwizard.configuration.ApplicationConfiguration;
import io.moneyinthesky.dashboard.core.app.dropwizard.healthchecks.ApplicationHealthCheck;
import io.moneyinthesky.dashboard.core.app.dropwizard.healthchecks.DashboardDataServiceHealthCheck;
import io.moneyinthesky.dashboard.core.app.guice.DashboardModule;
import io.moneyinthesky.dashboard.core.resources.DashboardResource;
import io.moneyinthesky.dashboard.core.resources.SettingsResource;

import static com.google.inject.Guice.createInjector;

public class Application extends io.dropwizard.Application<ApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    public void run(ApplicationConfiguration configuration, Environment environment) throws Exception {
        Injector injector = createInjector(new DashboardModule(configuration));

        environment.healthChecks().register("application", injector.getInstance(ApplicationHealthCheck.class));
        environment.healthChecks().register("dashboardDataService", injector.getInstance(DashboardDataServiceHealthCheck.class));

        environment.jersey().setUrlPattern("/api/*");
        environment.jersey().register(injector.getInstance(DashboardResource.class));
        environment.jersey().register(injector.getInstance(SettingsResource.class));
    }

    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }
}
