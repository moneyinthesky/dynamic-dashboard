package io.moneyinthesky.dashboard.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.moneyinthesky.dashboard.data.Settings;
import io.moneyinthesky.dashboard.nodediscovery.UrlPatternMethod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static io.moneyinthesky.dashboard.patterns.ExplodableString.explode;
import static java.lang.System.currentTimeMillis;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardDataDaoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    private UrlPatternMethod urlPatternMethod;

    @Mock
    private SettingsDao settingsDao;

	@Mock
	private ObjectMapper objectMapper;

    @InjectMocks
    private DashboardDataDao dashboardDataDao;

    @Before
    public void setUp() throws IOException {
        when(settingsDao.readSettings()).thenReturn(getSettings());

        String format = "http://h1wap[01-20]-v01.dcm.stg2.ovp.bskyb.com/dcm/private/status/info";
        when(urlPatternMethod.generateNodeUrls(ImmutableMap.of("urlPattern", format)))
                .thenReturn(newArrayList(explode(format)));

        format = "http://dcm-app-v02-[01-10][a-b].u3euw1.api.bskyb.com/dcm/private/status/info";
        when(urlPatternMethod.generateNodeUrls(ImmutableMap.of("urlPattern", format)))
                .thenReturn(newArrayList(explode(format)));
    }

    @Test
    @Ignore
    public void testGenerateDashboardData() throws IOException {
        long start = currentTimeMillis();
//        dashboardDataDao.populateDashboardData();
        long end = currentTimeMillis();

        System.out.println("Time taken: " + (end-start)/1000d);
    }

    private static Settings getSettings() throws IOException {
        return MAPPER.readValue(getResource("settings.json"), Settings.class);
    }
}
