package io.moneyinthesky.dashboard.core.app;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.core.service.DashboardDataService;

import static com.codahale.metrics.health.HealthCheck.Result.*;

public class DashboardDataServiceHealthCheck extends HealthCheck {

    private DashboardDataService dashboardDataService;

    @Inject
    public DashboardDataServiceHealthCheck(DashboardDataService dashboardDataService) {
        this.dashboardDataService = dashboardDataService;
    }

    @Override
    protected Result check() throws Exception {
        return dashboardDataService.isRunning() ? healthy() : unhealthy("DashboardDataService is not running");
    }
}
