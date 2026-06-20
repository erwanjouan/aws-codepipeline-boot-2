package bluegreen.model;

import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

import java.util.Objects;

public class InstanceInfo extends EC2MetadataUtils.InstanceInfo {

    private String autoScalingGroup;
    private String autoScalingGroupState;

    public InstanceInfo(final String pendingTime, final String instanceType, final String imageId, final String instanceId, final String[] billingProducts, final String architecture, final String accountId, final String kernelId, final String ramdiskId, final String region, final String version, final String availabilityZone, final String privateIp, final String[] devpayProductCodes, final String[] marketplaceProductCodes, final String autoScalingGroup, final String autoScalingState) {
        super(pendingTime, instanceType, imageId, instanceId, billingProducts, architecture, accountId, kernelId, ramdiskId, region, version, availabilityZone, privateIp, devpayProductCodes, marketplaceProductCodes);
        this.autoScalingGroup = autoScalingGroup;
        this.autoScalingGroupState = autoScalingState;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String pendingTime;
        private String instanceType;
        private String imageId;
        private String instanceId;
        private String[] billingProducts;
        private String architecture;
        private String accountId;
        private String kernelId;
        private String ramdiskId;
        private String region;
        private String version;
        private String availabilityZone;
        private String privateIp;
        private String[] devpayProductCodes;
        private String[] marketplaceProductCodes;
        private String autoScalingGroup;
        private String autoScalingState;

        public Builder pendingTime(String pendingTime) {
            this.pendingTime = pendingTime;
            return this;
        }

        public Builder instanceType(String instanceType) {
            this.instanceType = instanceType;
            return this;
        }

        public Builder imageId(String imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder instanceId(String instanceId) {
            this.instanceId = instanceId;
            return this;
        }

        public Builder billingProducts(String[] billingProducts) {
            this.billingProducts = billingProducts;
            return this;
        }

        public Builder architecture(String architecture) {
            this.architecture = architecture;
            return this;
        }

        public Builder accountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder kernelId(String kernelId) {
            this.kernelId = kernelId;
            return this;
        }

        public Builder ramdiskId(String ramdiskId) {
            this.ramdiskId = ramdiskId;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder availabilityZone(String availabilityZone) {
            this.availabilityZone = availabilityZone;
            return this;
        }

        public Builder privateIp(String privateIp) {
            this.privateIp = privateIp;
            return this;
        }

        public Builder devpayProductCodes(String[] devpayProductCodes) {
            this.devpayProductCodes = devpayProductCodes;
            return this;
        }

        public Builder marketplaceProductCodes(String[] marketplaceProductCodes) {
            this.marketplaceProductCodes = marketplaceProductCodes;
            return this;
        }

        public Builder autoScalingGroup(String autoScalingGroup) {
            this.autoScalingGroup = autoScalingGroup;
            return this;
        }

        public Builder autoScalingState(String autoScalingState) {
            this.autoScalingState = autoScalingState;
            return this;
        }

        public InstanceInfo build() {
            return new InstanceInfo(pendingTime, instanceType, imageId, instanceId, billingProducts, architecture,
                    accountId, kernelId, ramdiskId, region, version, availabilityZone, privateIp,
                    devpayProductCodes, marketplaceProductCodes, autoScalingGroup, autoScalingState);
        }
    }

    public String getAutoScalingGroup() {
        return autoScalingGroup;
    }

    public void setAutoScalingGroup(String autoScalingGroup) {
        this.autoScalingGroup = autoScalingGroup;
    }

    public String getAutoScalingGroupState() {
        return autoScalingGroupState;
    }

    public void setAutoScalingGroupState(String autoScalingGroupState) {
        this.autoScalingGroupState = autoScalingGroupState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceInfo that = (InstanceInfo) o;
        return Objects.equals(autoScalingGroup, that.autoScalingGroup)
                && Objects.equals(autoScalingGroupState, that.autoScalingGroupState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(autoScalingGroup, autoScalingGroupState);
    }

    @Override
    public String toString() {
        return "InstanceInfo{autoScalingGroup=" + autoScalingGroup + ", autoScalingGroupState=" + autoScalingGroupState + "}";
    }
}
