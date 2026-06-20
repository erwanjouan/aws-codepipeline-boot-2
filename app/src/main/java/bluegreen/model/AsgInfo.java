package bluegreen.model;

import java.util.Objects;

public class AsgInfo {

    private String instanceId;
    private String asgName;
    private String healthStatus;
    private String lifecycleState;
    private String lcVersion;
    private String launchTemplateVersion;

    private AsgInfo(Builder b) {
        this.instanceId = b.instanceId;
        this.asgName = b.asgName;
        this.healthStatus = b.healthStatus;
        this.lifecycleState = b.lifecycleState;
        this.lcVersion = b.lcVersion;
        this.launchTemplateVersion = b.launchTemplateVersion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String instanceId;
        private String asgName;
        private String healthStatus;
        private String lifecycleState;
        private String lcVersion;
        private String launchTemplateVersion;

        public Builder instanceId(String instanceId) {
            this.instanceId = instanceId;
            return this;
        }

        public Builder asgName(String asgName) {
            this.asgName = asgName;
            return this;
        }

        public Builder healthStatus(String healthStatus) {
            this.healthStatus = healthStatus;
            return this;
        }

        public Builder lifecycleState(String lifecycleState) {
            this.lifecycleState = lifecycleState;
            return this;
        }

        public Builder lcVersion(String lcVersion) {
            this.lcVersion = lcVersion;
            return this;
        }

        public Builder launchTemplateVersion(String launchTemplateVersion) {
            this.launchTemplateVersion = launchTemplateVersion;
            return this;
        }

        public AsgInfo build() {
            return new AsgInfo(this);
        }
    }

    public final static AsgInfo DEFAULT_ASG_INFO = AsgInfo.builder()
            .asgName("")
            .lifecycleState("")
            .build();

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getAsgName() {
        return asgName;
    }

    public void setAsgName(String asgName) {
        this.asgName = asgName;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getLifecycleState() {
        return lifecycleState;
    }

    public void setLifecycleState(String lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public String getLcVersion() {
        return lcVersion;
    }

    public void setLcVersion(String lcVersion) {
        this.lcVersion = lcVersion;
    }

    public String getLaunchTemplateVersion() {
        return launchTemplateVersion;
    }

    public void setLaunchTemplateVersion(String launchTemplateVersion) {
        this.launchTemplateVersion = launchTemplateVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsgInfo that = (AsgInfo) o;
        return Objects.equals(instanceId, that.instanceId)
                && Objects.equals(asgName, that.asgName)
                && Objects.equals(healthStatus, that.healthStatus)
                && Objects.equals(lifecycleState, that.lifecycleState)
                && Objects.equals(lcVersion, that.lcVersion)
                && Objects.equals(launchTemplateVersion, that.launchTemplateVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId, asgName, healthStatus, lifecycleState, lcVersion, launchTemplateVersion);
    }

    @Override
    public String toString() {
        return "AsgInfo{instanceId=" + instanceId + ", asgName=" + asgName + ", healthStatus=" + healthStatus + ", lifecycleState=" + lifecycleState + ", lcVersion=" + lcVersion + ", launchTemplateVersion=" + launchTemplateVersion + "}";
    }
}
