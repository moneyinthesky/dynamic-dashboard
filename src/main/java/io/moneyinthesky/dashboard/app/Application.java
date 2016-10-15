package io.moneyinthesky.dashboard.app;

import com.google.inject.Injector;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.moneyinthesky.dashboard.resources.DashboardResource;
import io.moneyinthesky.dashboard.resources.SettingsResource;
import io.moneyinthesky.dashboard.resources.StatusResource;

import static com.google.inject.Guice.createInjector;

public class Application extends io.dropwizard.Application<ApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    public void run(ApplicationConfiguration configuration, Environment environment) throws Exception {
        Injector injector = createInjector(new DashboardModule());

        environment.jersey().setUrlPattern("/api/*");
        environment.jersey().register(injector.getInstance(StatusResource.class));
        environment.jersey().register(injector.getInstance(DashboardResource.class));
        environment.jersey().register(injector.getInstance(SettingsResource.class));
    }

    public void initialize(Bootstrap<ApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/", "index.html"));
    }
}
