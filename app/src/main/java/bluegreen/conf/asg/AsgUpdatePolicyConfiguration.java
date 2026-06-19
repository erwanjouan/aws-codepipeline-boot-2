package bluegreen.conf.asg;

import bluegreen.conf.ec2.Ec2CodeDeployConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;

import static bluegreen.model.Constant.ASG_EC2_IMAGE_BUILDER_PROFILE;
import static bluegreen.model.Constant.ASG_LIFECYCLE_HOOK_PROFILE;
import static bluegreen.model.Constant.ASG_ROLLING_UPDATE_PROFILE;
import static bluegreen.model.Constant.CLOUDFRONT_PROFILE;

@Configuration
@Profile({ASG_ROLLING_UPDATE_PROFILE, ASG_LIFECYCLE_HOOK_PROFILE, CLOUDFRONT_PROFILE, ASG_EC2_IMAGE_BUILDER_PROFILE})
public class AsgUpdatePolicyConfiguration extends Ec2CodeDeployConfiguration {

    @Bean
    public AutoScalingClient amazonAutoScalingClient() {
        return AutoScalingClient.builder()
                .region(REGION)
                .build();
    }

    @Bean
    public ElasticLoadBalancingV2Client elasticLoadBalancingV2Client() {
        return ElasticLoadBalancingV2Client.builder()
                .region(REGION)
                .build();
    }
}
