package bluegreen.conf.opsworks;

import bluegreen.conf.AwsConfiguration;
import bluegreen.model.InstanceInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;
import software.amazon.awssdk.services.opsworks.OpsWorksClient;

import static bluegreen.model.Constant.OPSWORKS_STACKS_PROFILE;

@Configuration
@Profile(OPSWORKS_STACKS_PROFILE)
public class OpsWorksStacksConfiguration implements AwsConfiguration {

    public static final String OPSWORKS_STACKS_CLIENT = "OpsWorksStacksClient";
    public static final String OPSWORKS_CLIENT = "OpsWorksClient";

    @Bean
    @Qualifier(OPSWORKS_STACKS_CLIENT)
    public OpsWorksClient opsWorksStacksClient() {
        return OpsWorksClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    @Bean
    @Qualifier(OPSWORKS_CLIENT)
    public OpsWorksClient opsWorksClient() {
        return OpsWorksClient.builder()
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
