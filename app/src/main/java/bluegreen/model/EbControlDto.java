package bluegreen.model;

import java.util.Map;
import java.util.Objects;

public class EbControlDto {
    private InstanceInfo instanceInfo;
    private Map<String, Object> deploymentInfo;
    private ControlTable controlTable;
    private String platformType;

    private EbControlDto(Builder b) {
        this.instanceInfo = b.instanceInfo;
        this.deploymentInfo = b.deploymentInfo;
        this.controlTable = b.controlTable;
        this.platformType = b.platformType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InstanceInfo instanceInfo;
        private Map<String, Object> deploymentInfo;
        private ControlTable controlTable;
        private String platformType;

        public Builder instanceInfo(InstanceInfo instanceInfo) {
            this.instanceInfo = instanceInfo;
            return this;
        }

        public Builder deploymentInfo(Map<String, Object> deploymentInfo) {
            this.deploymentInfo = deploymentInfo;
            return this;
        }

        public Builder controlTable(ControlTable controlTable) {
            this.controlTable = controlTable;
            return this;
        }

        public Builder platformType(String platformType) {
            this.platformType = platformType;
            return this;
        }

        public EbControlDto build() {
            return new EbControlDto(this);
        }
    }

    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public void setInstanceInfo(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    public Map<String, Object> getDeploymentInfo() {
        return deploymentInfo;
    }

    public void setDeploymentInfo(Map<String, Object> deploymentInfo) {
        this.deploymentInfo = deploymentInfo;
    }

    public ControlTable getControlTable() {
        return controlTable;
    }

    public void setControlTable(ControlTable controlTable) {
        this.controlTable = controlTable;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EbControlDto that = (EbControlDto) o;
        return Objects.equals(instanceInfo, that.instanceInfo)
                && Objects.equals(deploymentInfo, that.deploymentInfo)
                && Objects.equals(controlTable, that.controlTable)
                && Objects.equals(platformType, that.platformType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceInfo, deploymentInfo, controlTable, platformType);
    }

    @Override
    public String toString() {
        return "EbControlDto{instanceInfo=" + instanceInfo + ", deploymentInfo=" + deploymentInfo + ", controlTable=" + controlTable + ", platformType=" + platformType + "}";
    }
}
