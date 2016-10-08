package io.moneyinthesky.dashboard.data;

import java.util.List;

public class PersistedSettings {
    private String title;
    private List<String> applications;

    public String getTitle() {
        return title;
    }

    public List<String> getApplications() {
        return applications;
    }
}
