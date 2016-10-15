package io.moneyinthesky.dashboard.data;

import java.util.List;
import java.util.Map;

public class DashboardData {

    private String timeGenerated;
    private List<DataCenterStatus> dataCenters;

    public String getTimeGenerated() {
        return timeGenerated;
    }

    public List<DataCenterStatus> getDataCenters() {
        return dataCenters;
    }

    public static class DataCenterStatus {
        private String name;
        private List<String> environments;
        private Map<String, ApplicationStatus> applications;

        public String getName() {
            return name;
        }

        public List<String> getEnvironments() {
            return environments;
        }

        public Map<String, ApplicationStatus> getApplications() {
            return applications;
        }
    }

    public static class ApplicationStatus {
        private Map<String, EnvironmentStatus> environments;

        public Map<String, EnvironmentStatus> getEnvironments() {
            return environments;
        }
    }

    public static class EnvironmentStatus {
        private Map<String, VersionStatus> versions;

        public Map<String, VersionStatus> getVersions() {
            return versions;
        }
    }

    public static class VersionStatus {
        private Integer nodesUp;
        private Integer nodesDown;

        public Integer getNodesUp() {
            return nodesUp;
        }

        public Integer getNodesDown() {
            return nodesDown;
        }
    }

}
