package bluegreen.conf.lightsail;

import bluegreen.conf.AwsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.lightsail.LightsailClient;

import static bluegreen.model.Constant.LIGHTSAIL_CONTAINER_PROFILE;

@Configuration
@Profile(LIGHTSAIL_CONTAINER_PROFILE)
public class LightSailContainerConfiguration implements AwsConfiguration {

    @Bean
    public LightsailClient lightSailClient() {
        return LightsailClient.builder()
                .region(REGION)
                .build();
    }
}
