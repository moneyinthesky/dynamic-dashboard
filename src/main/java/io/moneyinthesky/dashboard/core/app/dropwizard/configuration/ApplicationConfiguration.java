package io.moneyinthesky.dashboard.core.app.dropwizard.configuration;

import io.dropwizard.Configuration;

public class ApplicationConfiguration extends Configuration {

    private String settingsFile;
    private String awsResponseFile;

    private ConnectivityConfiguration connectivityConfiguration;

    private NodeStatusRetrievalConfiguration nodeStatusRetrievalConfiguration;

    public String getSettingsFile() {
        return settingsFile;
    }

    public String getAwsResponseFile() {
        return awsResponseFile;
    }

    public ConnectivityConfiguration getConnectivityConfiguration() {
        return connectivityConfiguration;
    }

    public NodeStatusRetrievalConfiguration getNodeStatusRetrievalConfiguration() {
        return nodeStatusRetrievalConfiguration;
    }
}
