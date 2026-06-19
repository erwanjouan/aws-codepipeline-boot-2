package bluegreen.service.elb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetHealthRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetHealthResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetGroup;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetHealthDescription;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static bluegreen.model.Constant.ASG_CODE_DEPLOY_EC2_PROFILE;
import static bluegreen.model.Constant.ASG_EC2_IMAGE_BUILDER_PROFILE;
import static bluegreen.model.Constant.ASG_LIFECYCLE_HOOK_PROFILE;
import static bluegreen.model.Constant.ASG_ROLLING_UPDATE_PROFILE;
import static bluegreen.model.Constant.CLOUDFRONT_PROFILE;


@Service
@Profile({ASG_ROLLING_UPDATE_PROFILE, ASG_LIFECYCLE_HOOK_PROFILE, CLOUDFRONT_PROFILE, ASG_CODE_DEPLOY_EC2_PROFILE,
        ASG_EC2_IMAGE_BUILDER_PROFILE})
public class ElasticLoadBalancerService {

    @Autowired
    private ElasticLoadBalancingV2Client elasticLoadBalancingV2Client;

    public Map<String, TargetHealthDescription> get() {
        return this.elasticLoadBalancingV2Client.describeTargetGroups()
                .targetGroups()
                .stream()
                .map(TargetGroup::targetGroupArn)
                .map(tgArn -> DescribeTargetHealthRequest.builder().targetGroupArn(tgArn).build())
                .map(this.elasticLoadBalancingV2Client::describeTargetHealth)
                .map(DescribeTargetHealthResponse::targetHealthDescriptions)
                .flatMap(Collection::stream)
                .filter(targetHealthDescription -> targetHealthDescription.target().id().startsWith("i-"))
                .collect(Collectors.toMap(
                        targetHealthDescription -> targetHealthDescription.target().id(),
                        Function.identity()
                ));
    }
}
