package bluegreen.model;

import software.amazon.awssdk.services.ecs.model.DeploymentConfiguration;
import software.amazon.awssdk.services.ecs.model.DeploymentController;

import java.util.Objects;

public class EcsFargateAlbControlDto {
    private String taskArn;
    private String region;
    private ControlTable controlTable;
    private DeploymentConfiguration deploymentConfiguration;
    private DeploymentController deploymentController;
    private String stats;
    private Integer cpu;

    private EcsFargateAlbControlDto(Builder b) {
        this.taskArn = b.taskArn;
        this.region = b.region;
        this.controlTable = b.controlTable;
        this.deploymentConfiguration = b.deploymentConfiguration;
        this.deploymentController = b.deploymentController;
        this.stats = b.stats;
        this.cpu = b.cpu;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String taskArn;
        private String region;
        private ControlTable controlTable;
        private DeploymentConfiguration deploymentConfiguration;
        private DeploymentController deploymentController;
        private String stats;
        private Integer cpu;

        public Builder taskArn(String taskArn) {
            this.taskArn = taskArn;
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

        public Builder deploymentConfiguration(DeploymentConfiguration deploymentConfiguration) {
            this.deploymentConfiguration = deploymentConfiguration;
            return this;
        }

        public Builder deploymentController(DeploymentController deploymentController) {
            this.deploymentController = deploymentController;
            return this;
        }

        public Builder stats(String stats) {
            this.stats = stats;
            return this;
        }

        public Builder cpu(Integer cpu) {
            this.cpu = cpu;
            return this;
        }

        public EcsFargateAlbControlDto build() {
            return new EcsFargateAlbControlDto(this);
        }
    }

    public String getTaskArn() {
        return taskArn;
    }

    public void setTaskArn(String taskArn) {
        this.taskArn = taskArn;
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

    public DeploymentConfiguration getDeploymentConfiguration() {
        return deploymentConfiguration;
    }

    public void setDeploymentConfiguration(DeploymentConfiguration deploymentConfiguration) {
        this.deploymentConfiguration = deploymentConfiguration;
    }

    public DeploymentController getDeploymentController() {
        return deploymentController;
    }

    public void setDeploymentController(DeploymentController deploymentController) {
        this.deploymentController = deploymentController;
    }

    public String getStats() {
        return stats;
    }

    public void setStats(String stats) {
        this.stats = stats;
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
        EcsFargateAlbControlDto that = (EcsFargateAlbControlDto) o;
        return Objects.equals(taskArn, that.taskArn)
                && Objects.equals(region, that.region)
                && Objects.equals(controlTable, that.controlTable)
                && Objects.equals(deploymentConfiguration, that.deploymentConfiguration)
                && Objects.equals(deploymentController, that.deploymentController)
                && Objects.equals(stats, that.stats)
                && Objects.equals(cpu, that.cpu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskArn, region, controlTable, deploymentConfiguration, deploymentController, stats, cpu);
    }

    @Override
    public String toString() {
        return "EcsFargateAlbControlDto{taskArn=" + taskArn + ", region=" + region + ", controlTable=" + controlTable + ", deploymentConfiguration=" + deploymentConfiguration + ", deploymentController=" + deploymentController + ", stats=" + stats + ", cpu=" + cpu + "}";
    }
}
