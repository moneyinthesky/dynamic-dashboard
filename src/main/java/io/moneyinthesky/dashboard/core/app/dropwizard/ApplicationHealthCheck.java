package io.moneyinthesky.dashboard.core.app.dropwizard;

import com.codahale.metrics.health.HealthCheck;

import static com.codahale.metrics.health.HealthCheck.Result.*;

public class ApplicationHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return healthy();
    }
}
