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

    public void setTimeGenerated(String timeGenerated) {
        this.timeGenerated = timeGenerated;
    }

    public void setDataCenters(List<DataCenterStatus> dataCenters) {
        this.dataCenters = dataCenters;
    }

    public static class DataCenterStatus {
        private String name;
        private List<String> environments;
        private List<ApplicationStatus> applications;

        public String getName() {
            return name;
        }

        public List<String> getEnvironments() {
            return environments;
        }

        public List<ApplicationStatus> getApplications() {
            return applications;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEnvironments(List<String> environments) {
            this.environments = environments;
        }

        public void setApplications(List<ApplicationStatus> applications) {
            this.applications = applications;
        }
    }

    public static class ApplicationStatus {
        private String name;
        private Map<String, EnvironmentStatus> environmentStatuses;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, EnvironmentStatus> getEnvironmentStatuses() {
            return environmentStatuses;
        }

        public void setEnvironmentStatuses(Map<String, EnvironmentStatus> environmentStatuses) {
            this.environmentStatuses = environmentStatuses;
        }
    }

    public static class EnvironmentStatus {
        private List<NodeStatus> nodeStatuses;

        public List<NodeStatus> getNodeStatuses() {
            return nodeStatuses;
        }

        public void setNodeStatuses(List<NodeStatus> nodeStatuses) {
            this.nodeStatuses = nodeStatuses;
        }
    }

    public static class NodeStatus {
        private String url;
        private String version;
        private Boolean up;
        private String errorMessage;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Boolean getUp() {
            return up;
        }

        public void setUp(Boolean up) {
            this.up = up;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

}
