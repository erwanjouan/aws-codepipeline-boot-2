package bluegreen.service.asg;

import bluegreen.model.AsgInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingInstanceDetails;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingInstancesRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingInstancesResponse;
import software.amazon.awssdk.services.autoscaling.model.LaunchTemplateSpecification;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bluegreen.model.Constant.ASG_EC2_IMAGE_BUILDER_PROFILE;
import static bluegreen.model.Constant.ASG_LIFECYCLE_HOOK_PROFILE;
import static bluegreen.model.Constant.ASG_ROLLING_UPDATE_PROFILE;
import static bluegreen.model.Constant.CLOUDFRONT_PROFILE;
import static bluegreen.model.Constant.EB_EC2_JAR_PROFILE;
import static bluegreen.model.Constant.EB_MULTI_DOCKER_PROFILE;
import static bluegreen.model.Constant.EB_SINGLE_DOCKER_PROFILE;


@Service
@Profile({EB_SINGLE_DOCKER_PROFILE, EB_MULTI_DOCKER_PROFILE, EB_EC2_JAR_PROFILE,
        ASG_ROLLING_UPDATE_PROFILE, ASG_LIFECYCLE_HOOK_PROFILE, CLOUDFRONT_PROFILE,
        ASG_EC2_IMAGE_BUILDER_PROFILE})
public class AutoScalingGroupService {

    @Autowired
    private AutoScalingClient autoScalingClient;

    public Map<String, AsgInfo> getInstancesAsg(final List<String> instanceIds) {
        return this.getInstancesAsgDetails(instanceIds).stream()
                .map(autoScalingInstanceDetails -> AsgInfo.builder()
                        .instanceId(autoScalingInstanceDetails.instanceId())
                        .asgName(this.getAsgName(autoScalingInstanceDetails))
                        .healthStatus(autoScalingInstanceDetails.healthStatus())
                        .launchTemplateVersion(this.getLaunchTemplateVersion(autoScalingInstanceDetails))
                        .lifecycleState(autoScalingInstanceDetails.lifecycleState())
                        .build()
                ).collect(Collectors.toMap(AsgInfo::getInstanceId,
                        Function.identity(),
                        (a, b) -> a));
    }

    private String getLaunchTemplateVersion(final AutoScalingInstanceDetails autoScalingInstanceDetails) {
        return Optional.ofNullable(autoScalingInstanceDetails)
                .map(AutoScalingInstanceDetails::launchTemplate)
                .map(LaunchTemplateSpecification::launchTemplateName)
                .filter(Objects::nonNull)
                .map(launchTemplateName -> launchTemplateName.substring(0, 8))
                .orElse("");
    }

    private String getAsgName(final AutoScalingInstanceDetails autoScalingInstanceDetails) {
        final String[] splitted = autoScalingInstanceDetails.autoScalingGroupName().split("-");
        final int length = splitted.length;
        final String last = splitted[length - 1];
        return last;
    }

    private List<AutoScalingInstanceDetails> getInstancesAsgDetails(final List<String> instanceIds) {
        final DescribeAutoScalingInstancesRequest request = DescribeAutoScalingInstancesRequest.builder()
                .instanceIds(instanceIds)
                .build();
        final DescribeAutoScalingInstancesResponse response = this.autoScalingClient.describeAutoScalingInstances(request);
        return response.autoScalingInstances();
    }
}
