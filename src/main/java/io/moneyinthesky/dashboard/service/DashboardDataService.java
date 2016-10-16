package io.moneyinthesky.dashboard.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.dao.DashboardDataDao;
import io.moneyinthesky.dashboard.data.dashboard.DashboardData;
import org.slf4j.Logger;

import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedRateSchedule;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.slf4j.LoggerFactory.getLogger;

public class DashboardDataService extends AbstractScheduledService {

	private static final Logger logger = getLogger(DashboardDataService.class);

	private DashboardDataDao dashboardDataDao;

	private DashboardData cachedDashboardData;

	@Inject
	public DashboardDataService(DashboardDataDao dashboardDataDao) {
		this.dashboardDataDao = dashboardDataDao;

		logger.info("Starting Dashboard Data Service");
		super.startAsync();
	}

	public DashboardData getDashboardData() {
		return cachedDashboardData;
	}

	@Override
	protected void runOneIteration() throws Exception {
		logger.info("Populating dashboard...");
		long start = currentTimeMillis();
		cachedDashboardData = dashboardDataDao.populateDashboardData();
		logger.info("Time to populate dashboard: " + (currentTimeMillis() - start) / 1000d);
	}

	@Override
	protected Scheduler scheduler() {
		return newFixedRateSchedule(0, 60, SECONDS);
	}
}
