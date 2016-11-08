package io.moneyinthesky.dashboard.core.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.moneyinthesky.dashboard.core.data.dashboard.DashboardData;
import io.moneyinthesky.dashboard.core.data.settings.Settings;
import io.moneyinthesky.dashboard.nodediscovery.urlpattern.UrlPatternDiscoveryMethod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static io.moneyinthesky.dashboard.nodediscovery.urlpattern.PatternExploder.explode;
import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class DashboardDataDaoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    private UrlPatternDiscoveryMethod urlPatternMethod;

    @Mock
    private SettingsDao settingsDao;

	@Mock
	private ObjectMapper objectMapper;

    @InjectMocks
    private DashboardDataDao dashboardDataDao;

    @Before
    public void setUp() throws IOException {
        when(settingsDao.readSettings()).thenReturn(getSettings());

        String format = "http://h1wap[01-20]-v01.dcm.stg2.ovp.bskyb.com";
        when(urlPatternMethod.generateNodeUrls(ImmutableMap.of("urlPattern", format)))
                .thenReturn(newArrayList(explode(format)));

        format = "http://dcm-app-v02-[01-10][a-b].u3euw1.api.bskyb.com";
        when(urlPatternMethod.generateNodeUrls(ImmutableMap.of("urlPattern", format)))
                .thenReturn(newArrayList(explode(format)));

        when(objectMapper.readValue(anyString(), any(Class.class))).thenReturn(getSampleOutput());
    }

    @Test
    @Ignore
    public void testGenerateDashboardData() throws IOException {
        long start = currentTimeMillis();
        DashboardData data = dashboardDataDao.populateDashboardData();
        long end = currentTimeMillis();

        System.out.println("Time taken: " + (end-start)/1000d);

        assertThat(data.getTimeGenerated()).isNotNull();
    }

    private static Settings getSettings() throws IOException {
        return MAPPER.readValue(getResource("settings.json"), Settings.class);
    }

    private static Map<String, Object> getSampleOutput() throws IOException {
        return MAPPER.readValue(getResource("sample-output.json"), Map.class);
    }
}
