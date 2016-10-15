package io.moneyinthesky.dashboard.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.moneyinthesky.dashboard.data.Settings;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashboardDataDaoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    private SettingsDao settingsDao;

    @InjectMocks
    private DashboardDataDao dashboardDataDao;

    @Before
    public void setUp() throws IOException {
        when(settingsDao.readSettings()).thenReturn(getSettings());
    }

    @Test
    @Ignore
    public void testGenerateDashboardData() throws IOException {
        dashboardDataDao.generateDashboardData();
    }

    @Test
    @Ignore
    public void test() throws UnirestException {
        HttpResponse<String> response = (Unirest.get("http://dcm-app-v02-01a.d1euw1.api.bskyb.com/dcm/private/status/info").asString());
        System.out.println(response.getBody());
    }

    private static Settings getSettings() throws IOException {
        return MAPPER.readValue(getResource("settings.json"), Settings.class);
    }
}
