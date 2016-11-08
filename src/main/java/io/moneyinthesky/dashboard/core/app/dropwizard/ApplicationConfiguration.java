package io.moneyinthesky.dashboard.core.app.dropwizard;

import io.dropwizard.Configuration;

public class ApplicationConfiguration extends Configuration {

    private String settingsFile;
    private String awsResponseFile;

    public String getSettingsFile() {
        return settingsFile;
    }

    public String getAwsResponseFile() {
        return awsResponseFile;
    }
}
