package bluegreen.conf.apprunner;

import bluegreen.conf.AwsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;

import static bluegreen.model.Constant.APPRUNNER_CONTAINER_PROFILE;

@Configuration
@Profile(APPRUNNER_CONTAINER_PROFILE)
public class AppRunnerContainerConfiguration implements AwsConfiguration {

    @Bean
    public AppRunnerClient appRunnerClient() {
        return AppRunnerClient.builder()
                .region(REGION)
                .build();
    }
}
