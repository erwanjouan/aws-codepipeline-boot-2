package bluegreen.conf.ec2;

import bluegreen.conf.AwsConfiguration;
import bluegreen.model.InstanceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;

import static bluegreen.model.Constant.ASG_CODE_DEPLOY_EC2_PROFILE;

@Configuration
@Profile(ASG_CODE_DEPLOY_EC2_PROFILE)
public class Ec2CodeDeployConfiguration implements AwsConfiguration {

    @Bean
    public Ec2Client ec2Client() {
        return Ec2Client.builder()
                .region(REGION)
                .build();
    }

    @Bean
    public CodeDeployClient codeDeployClient() {
        return CodeDeployClient.builder()
                .region(REGION)
                .build();
    }

    @Bean
    public ElasticLoadBalancingV2Client elasticLoadBalancingV2Client() {
        return ElasticLoadBalancingV2Client.builder()
                .region(REGION)
                .build();
    }

    @Bean
    @Profile("!local")
    public EC2MetadataUtils.InstanceInfo instanceInfo() {
        return EC2MetadataUtils.getInstanceInfo();
    }

    @Bean
    @Profile("local")
    public InstanceInfo instanceInfoLocal() {
        return InstanceInfo.builder()
                .instanceId("local")
                .availabilityZone("none")
                .build();
    }
}
