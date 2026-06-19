package bluegreen.conf.eb;

import bluegreen.conf.AwsConfiguration;
import bluegreen.model.InstanceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;

import static bluegreen.model.Constant.EB_EC2_JAR_PROFILE;
import static bluegreen.model.Constant.EB_MULTI_DOCKER_PROFILE;
import static bluegreen.model.Constant.EB_SINGLE_DOCKER_PROFILE;

@Configuration
@Profile({EB_SINGLE_DOCKER_PROFILE, EB_MULTI_DOCKER_PROFILE, EB_EC2_JAR_PROFILE})
public class EbEc2JarConfiguration implements AwsConfiguration {

    @Bean
    public Ec2Client ec2Client() {
        return Ec2Client.builder()
                .region(REGION)
                .build();
    }

    @Bean
    public ElasticBeanstalkClient elasticBeanstalkClient() {
        return ElasticBeanstalkClient.builder()
                .region(REGION)
                .build();
    }

    @Bean
    public AutoScalingClient amazonAutoScalingClient() {
        return AutoScalingClient.builder()
                .region(REGION)
                .build();
    }

    @Bean
    @Profile("!local")
    public InstanceInfo instanceInfo() {
        final EC2MetadataUtils.InstanceInfo instanceInfo = EC2MetadataUtils.getInstanceInfo();
        return InstanceInfo.builder()
                .instanceId(instanceInfo.getInstanceId())
                .availabilityZone(instanceInfo.getAvailabilityZone())
                .build();
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
