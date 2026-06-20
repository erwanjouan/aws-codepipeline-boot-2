package bluegreen.conf.ecs;

import bluegreen.conf.AwsConfiguration;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.lang.management.ManagementFactory;

import static bluegreen.model.Constant.ALB_ECS_EC2_PROFILE;
import static bluegreen.model.Constant.ALB_ECS_FARGATE_PROFILE;
import static bluegreen.model.Constant.BLUE_GREEN_ECS_FARGATE_PROFILE;
import static bluegreen.model.Constant.DISASTER_RECOVERY_PROFILE;
import static bluegreen.model.Constant.PR_ECS_FARGATE_PROFILE;

@Configuration
@Profile({ALB_ECS_FARGATE_PROFILE, DISASTER_RECOVERY_PROFILE,
        BLUE_GREEN_ECS_FARGATE_PROFILE, PR_ECS_FARGATE_PROFILE,
        ALB_ECS_EC2_PROFILE})
public class EcsFargateAlbConfiguration implements AwsConfiguration {

    @Value("${AWS_DEFAULT_REGION}")
    private String defaultRegion;

    @Bean
    public EcsClient ecsClient() {
        return EcsClient.builder()
                .region(Region.of(this.defaultRegion))
                .build();
    }

    @Bean
    public SsmClient ssmClient() {
        return SsmClient.builder()
                .region(Region.of(this.defaultRegion))
                .build();
    }

    @Bean
    public OperatingSystemMXBean operatingSystem() {
        return ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

}
