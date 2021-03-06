package io.moneyinthesky.dashboard.core.service;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.inject.Inject;
import io.moneyinthesky.dashboard.core.aspects.LogExecutionTime;
import io.moneyinthesky.dashboard.core.dao.DashboardDataDao;
import io.moneyinthesky.dashboard.core.data.dashboard.DashboardData;
import org.slf4j.Logger;

import static com.google.common.util.concurrent.AbstractScheduledService.Scheduler.newFixedRateSchedule;
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

	public void forceDataRefresh() throws Exception {
		runOneIteration();
	}

	@Override
	@LogExecutionTime
	protected synchronized void runOneIteration() throws Exception {
		logger.info("Populating dashboard...");
		try {
			cachedDashboardData = dashboardDataDao.populateDashboardData();
		} catch(Exception e) {
			logger.error("Caught exception from dashboardDataDao", e);
		}
	}

	@Override
	protected Scheduler scheduler() {
		return newFixedRateSchedule(0, 45, SECONDS);
	}
}
