package bluegreen.service.lightsail;

import bluegreen.model.ControlTable;
import bluegreen.model.LightSailControlDto;
import bluegreen.service.WatchAwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lightsail.LightsailClient;
import software.amazon.awssdk.services.lightsail.model.ContainerService;
import software.amazon.awssdk.services.lightsail.model.GetContainerServicesRequest;
import software.amazon.awssdk.services.lightsail.model.GetContainerServicesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static bluegreen.model.Constant.ALB_ECS_FARGATE_PROFILE;
import static bluegreen.model.Constant.LIGHTSAIL_CONTAINER_PROFILE;
import static bluegreen.model.Constant.PROJECT_DEPLOYMENT_NAME_ENV;

@Service
@Profile(LIGHTSAIL_CONTAINER_PROFILE)
public class WatchLightSailContainerService implements WatchAwsService<LightSailControlDto> {

    public static final List<String> HEADERS = List.of("serviceName", "power",
            "createdAt", "state", "version");

    @Autowired
    private LightsailClient lightsailClient;

    @Value(PROJECT_DEPLOYMENT_NAME_ENV)
    private String projectDeploymentName;

    @Override
    public LightSailControlDto watch() {
        final List<Map<String, Object>> rows = new ArrayList<>();

        final GetContainerServicesRequest request = GetContainerServicesRequest.builder()
                .serviceName(this.projectDeploymentName)
                .build();
        final GetContainerServicesResponse response = this.lightsailClient.getContainerServices(request);

        for (final ContainerService containerService : response.containerServices()) {
            rows.add(
                    Map.of(
                            "serviceName", containerService.containerServiceName(),
                            "power", containerService.power(),
                            "createdAt", containerService.currentDeployment().createdAt(),
                            "state", containerService.currentDeployment().stateAsString(),
                            "version", containerService.currentDeployment().version()
                    )
            );
        }
        final ControlTable controlTable = ControlTable.builder()
                .tableName(ALB_ECS_FARGATE_PROFILE)
                .headers(HEADERS)
                .rows(rows)
                .build();
        
        return LightSailControlDto.builder()
                .controlTable(controlTable)
                .build();
    }
}
