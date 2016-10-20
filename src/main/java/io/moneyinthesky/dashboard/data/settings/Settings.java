package io.moneyinthesky.dashboard.data.settings;

import java.util.List;
import java.util.Map;

public class Settings {
    private String title;
    private List<String> applications;
    private Map<String, Map<String, String>> applicationConfig;
    private List<DataCenter> dataCenters;
    private Map<String, Map<String, Object>> plugins;

    public String getTitle() {
        return title;
    }

    public List<String> getApplications() {
        return applications;
    }

    public List<DataCenter> getDataCenters() {
        return dataCenters;
    }

    public Map<String, Map<String, String>> getApplicationConfig() {
        return applicationConfig;
    }

    public Map<String, Map<String, Object>> getPlugins() {
        return plugins;
    }
}
