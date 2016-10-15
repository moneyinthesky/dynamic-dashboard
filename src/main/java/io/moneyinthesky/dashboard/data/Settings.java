package io.moneyinthesky.dashboard.data;

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

	public static class DataCenter {
        private String name;
        private List<Environment> environments;

        public String getName() {
            return name;
        }

        public List<Environment> getEnvironments() {
            return environments;
        }
    }

    public static class Environment {
        private String name;
        private String nodeDiscoveryMethod;
        private Map<String, Map<String, String>> applicationConfig;

        public String getName() {
            return name;
        }

        public String getNodeDiscoveryMethod() {
            return nodeDiscoveryMethod;
        }

        public Map<String, Map<String, String>> getApplicationConfig() {
            return applicationConfig;
        }
    }
}
