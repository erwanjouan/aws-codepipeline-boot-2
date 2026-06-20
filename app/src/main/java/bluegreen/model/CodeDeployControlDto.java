package bluegreen.model;

import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

import java.util.Objects;

public class CodeDeployControlDto {
    private EC2MetadataUtils.InstanceInfo instanceInfo;
    private ControlTable controlTable;
    private String deploymentConfigName;
    private String deploymentType;
    private String deploymentOption;

    private CodeDeployControlDto(Builder b) {
        this.instanceInfo = b.instanceInfo;
        this.controlTable = b.controlTable;
        this.deploymentConfigName = b.deploymentConfigName;
        this.deploymentType = b.deploymentType;
        this.deploymentOption = b.deploymentOption;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EC2MetadataUtils.InstanceInfo instanceInfo;
        private ControlTable controlTable;
        private String deploymentConfigName;
        private String deploymentType;
        private String deploymentOption;

        public Builder instanceInfo(EC2MetadataUtils.InstanceInfo instanceInfo) {
            this.instanceInfo = instanceInfo;
            return this;
        }

        public Builder controlTable(ControlTable controlTable) {
            this.controlTable = controlTable;
            return this;
        }

        public Builder deploymentConfigName(String deploymentConfigName) {
            this.deploymentConfigName = deploymentConfigName;
            return this;
        }

        public Builder deploymentType(String deploymentType) {
            this.deploymentType = deploymentType;
            return this;
        }

        public Builder deploymentOption(String deploymentOption) {
            this.deploymentOption = deploymentOption;
            return this;
        }

        public CodeDeployControlDto build() {
            return new CodeDeployControlDto(this);
        }
    }

    public EC2MetadataUtils.InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public void setInstanceInfo(EC2MetadataUtils.InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    public ControlTable getControlTable() {
        return controlTable;
    }

    public void setControlTable(ControlTable controlTable) {
        this.controlTable = controlTable;
    }

    public String getDeploymentConfigName() {
        return deploymentConfigName;
    }

    public void setDeploymentConfigName(String deploymentConfigName) {
        this.deploymentConfigName = deploymentConfigName;
    }

    public String getDeploymentType() {
        return deploymentType;
    }

    public void setDeploymentType(String deploymentType) {
        this.deploymentType = deploymentType;
    }

    public String getDeploymentOption() {
        return deploymentOption;
    }

    public void setDeploymentOption(String deploymentOption) {
        this.deploymentOption = deploymentOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeDeployControlDto that = (CodeDeployControlDto) o;
        return Objects.equals(instanceInfo, that.instanceInfo)
                && Objects.equals(controlTable, that.controlTable)
                && Objects.equals(deploymentConfigName, that.deploymentConfigName)
                && Objects.equals(deploymentType, that.deploymentType)
                && Objects.equals(deploymentOption, that.deploymentOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceInfo, controlTable, deploymentConfigName, deploymentType, deploymentOption);
    }

    @Override
    public String toString() {
        return "CodeDeployControlDto{instanceInfo=" + instanceInfo + ", controlTable=" + controlTable + ", deploymentConfigName=" + deploymentConfigName + ", deploymentType=" + deploymentType + ", deploymentOption=" + deploymentOption + "}";
    }
}
