package bluegreen.model;

import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

import java.util.Objects;

public class OpsWorksStacksControlDto {
    private String functionName;
    private EC2MetadataUtils.InstanceInfo instanceInfo;
    private ControlTable controlTable;

    private OpsWorksStacksControlDto(Builder b) {
        this.functionName = b.functionName;
        this.instanceInfo = b.instanceInfo;
        this.controlTable = b.controlTable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String functionName;
        private EC2MetadataUtils.InstanceInfo instanceInfo;
        private ControlTable controlTable;

        public Builder functionName(String functionName) {
            this.functionName = functionName;
            return this;
        }

        public Builder instanceInfo(EC2MetadataUtils.InstanceInfo instanceInfo) {
            this.instanceInfo = instanceInfo;
            return this;
        }

        public Builder controlTable(ControlTable controlTable) {
            this.controlTable = controlTable;
            return this;
        }

        public OpsWorksStacksControlDto build() {
            return new OpsWorksStacksControlDto(this);
        }
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpsWorksStacksControlDto that = (OpsWorksStacksControlDto) o;
        return Objects.equals(functionName, that.functionName)
                && Objects.equals(instanceInfo, that.instanceInfo)
                && Objects.equals(controlTable, that.controlTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, instanceInfo, controlTable);
    }

    @Override
    public String toString() {
        return "OpsWorksStacksControlDto{functionName=" + functionName + ", instanceInfo=" + instanceInfo + ", controlTable=" + controlTable + "}";
    }
}
