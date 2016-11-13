package io.moneyinthesky.dashboard.core.app.dropwizard;

public class ConnectivityConfiguration {
    private long connectionTimeout;
    private long socketTimeout;

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public long getSocketTimeout() {
        return socketTimeout;
    }
}
