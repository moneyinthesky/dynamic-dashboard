package io.moneyinthesky.dashboard.core.data.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public class Settings {
    private String title = "Dynamic Dashboard";
    private List<String> applications = newArrayList();
    private Map<String, Map<String, String>> applicationConfig = new HashMap<>();
    private List<DataCenter> dataCenters = newArrayList();
    private Map<String, Map<String, Object>> plugins = new HashMap<>();

    public Settings() {
        this.plugins.put("aws", new HashMap<>());

        Map<String, Object> fleetConfiguration = new HashMap<>();
        fleetConfiguration.put("restApiUrls", newArrayList());
        this.plugins.put("fleet", fleetConfiguration);
    }

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
