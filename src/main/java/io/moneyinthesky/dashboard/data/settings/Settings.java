package io.moneyinthesky.dashboard.data.settings;

import java.util.List;
import java.util.Map;

public class Settings {
    private String title;
    private List<String> applications;
    private List<DataCenter> dataCenters;

    public String getTitle() {
        return title;
    }

    public List<String> getApplications() {
        return applications;
    }

    public List<DataCenter> getDataCenters() {
        return dataCenters;
    }


}
