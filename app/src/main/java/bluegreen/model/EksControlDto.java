package bluegreen.model;

import java.util.Objects;

public class EksControlDto {
    private String podName;
    private String region;
    private ControlTable controlTable;
    private Integer cpu;

    private EksControlDto(Builder b) {
        this.podName = b.podName;
        this.region = b.region;
        this.controlTable = b.controlTable;
        this.cpu = b.cpu;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String podName;
        private String region;
        private ControlTable controlTable;
        private Integer cpu;

        public Builder podName(String podName) {
            this.podName = podName;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder controlTable(ControlTable controlTable) {
            this.controlTable = controlTable;
            return this;
        }

        public Builder cpu(Integer cpu) {
            this.cpu = cpu;
            return this;
        }

        public EksControlDto build() {
            return new EksControlDto(this);
        }
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public ControlTable getControlTable() {
        return controlTable;
    }

    public void setControlTable(ControlTable controlTable) {
        this.controlTable = controlTable;
    }

    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EksControlDto that = (EksControlDto) o;
        return Objects.equals(podName, that.podName)
                && Objects.equals(region, that.region)
                && Objects.equals(controlTable, that.controlTable)
                && Objects.equals(cpu, that.cpu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(podName, region, controlTable, cpu);
    }

    @Override
    public String toString() {
        return "EksControlDto{podName=" + podName + ", region=" + region + ", controlTable=" + controlTable + ", cpu=" + cpu + "}";
    }
}
