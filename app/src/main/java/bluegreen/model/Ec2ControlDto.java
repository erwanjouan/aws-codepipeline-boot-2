package bluegreen.model;

import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

import java.util.Objects;

public class Ec2ControlDto {
    private EC2MetadataUtils.InstanceInfo instanceInfo;
    private ControlTable controlTable;

    private Ec2ControlDto(Builder b) {
        this.instanceInfo = b.instanceInfo;
        this.controlTable = b.controlTable;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EC2MetadataUtils.InstanceInfo instanceInfo;
        private ControlTable controlTable;

        public Builder instanceInfo(EC2MetadataUtils.InstanceInfo instanceInfo) {
            this.instanceInfo = instanceInfo;
            return this;
        }

        public Builder controlTable(ControlTable controlTable) {
            this.controlTable = controlTable;
            return this;
        }

        public Ec2ControlDto build() {
            return new Ec2ControlDto(this);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ec2ControlDto that = (Ec2ControlDto) o;
        return Objects.equals(instanceInfo, that.instanceInfo)
                && Objects.equals(controlTable, that.controlTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceInfo, controlTable);
    }

    @Override
    public String toString() {
        return "Ec2ControlDto{instanceInfo=" + instanceInfo + ", controlTable=" + controlTable + "}";
    }
}
