package bluegreen.model;

import lombok.Builder;
import lombok.Data;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;

@Data
public class InstanceInfo extends EC2MetadataUtils.InstanceInfo {

    private String autoScalingGroup;
    private String autoScalingGroupState;
    
    @Builder
    public InstanceInfo(final String pendingTime, final String instanceType, final String imageId, final String instanceId, final String[] billingProducts, final String architecture, final String accountId, final String kernelId, final String ramdiskId, final String region, final String version, final String availabilityZone, final String privateIp, final String[] devpayProductCodes, final String[] marketplaceProductCodes, final String autoScalingGroup, final String autoScalingState) {
        super(pendingTime, instanceType, imageId, instanceId, billingProducts, architecture, accountId, kernelId, ramdiskId, region, version, availabilityZone, privateIp, devpayProductCodes, marketplaceProductCodes);
        this.autoScalingGroup = autoScalingGroup;
        this.autoScalingGroupState = autoScalingState;
    }
}
