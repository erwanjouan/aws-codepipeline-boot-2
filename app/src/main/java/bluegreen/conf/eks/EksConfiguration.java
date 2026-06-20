package bluegreen.conf.eks;

import bluegreen.conf.AwsConfiguration;
import com.sun.management.OperatingSystemMXBean;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import static bluegreen.model.Constant.EKS_FARGATE_PROFILE;

@Configuration
@Profile({EKS_FARGATE_PROFILE})
public class EksConfiguration implements AwsConfiguration {

    @Value("${AWS_DEFAULT_REGION}")
    private String defaultRegion;

    @Bean
    public CoreV1Api coreV1Api() throws IOException {
        final ApiClient client = Config.defaultClient();
        return new CoreV1Api(client);
    }

    @Bean
    public AppsV1Api appsV1Api() throws IOException {
        final ApiClient client = Config.defaultClient();
        return new AppsV1Api(client);
    }

    @Bean
    public OperatingSystemMXBean operatingSystem() {
        return ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    }

    @Bean
    public SsmClient ssmClient() {
        return SsmClient.builder()
                .region(Region.of(this.defaultRegion))
                .build();
    }

}
