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
        private Map<String, EnvironmentStatus> environments;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, EnvironmentStatus> getEnvironments() {
            return environments;
        }

        public void setEnvironments(Map<String, EnvironmentStatus> environments) {
            this.environments = environments;
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
        private String version;
        private Integer nodesUp;
        private Integer nodesDown;

        public String getVersion() {
            return version;
        }

        public Integer getNodesUp() {
            return nodesUp;
        }

        public Integer getNodesDown() {
            return nodesDown;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void setNodesUp(Integer nodesUp) {
            this.nodesUp = nodesUp;
        }

        public void setNodesDown(Integer nodesDown) {
            this.nodesDown = nodesDown;
        }
    }

}
