package bluegreen.service.apprunner;

import bluegreen.model.AppRunnerControlDto;
import bluegreen.model.ControlTable;
import bluegreen.service.WatchAwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.apprunner.AppRunnerClient;
import software.amazon.awssdk.services.apprunner.model.ListServicesRequest;
import software.amazon.awssdk.services.apprunner.model.ListServicesResponse;
import software.amazon.awssdk.services.apprunner.model.ServiceSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static bluegreen.model.Constant.APPRUNNER_CONTAINER_PROFILE;
import static bluegreen.model.Constant.PROJECT_DEPLOYMENT_NAME_ENV;

@Service
@Profile(APPRUNNER_CONTAINER_PROFILE)
public class WatchAppRunnerContainerService implements WatchAwsService<AppRunnerControlDto> {

    public static final List<String> HEADERS = List.of("serviceName", "serviceId",
            "createdAt", "updatedAt", "status");

    @Autowired
    private AppRunnerClient appRunnerClient;

    @Value(PROJECT_DEPLOYMENT_NAME_ENV)
    private String projectDeploymentName;

    @Override
    public AppRunnerControlDto watch() {
        final List<Map<String, Object>> rows = new ArrayList<>();

        final ListServicesRequest request = ListServicesRequest.builder().build();
        final ListServicesResponse response = this.appRunnerClient.listServices(request);

        for (final ServiceSummary serviceSummary : response.serviceSummaryList()) {
            rows.add(
                    Map.of(
                            "serviceName", serviceSummary.serviceName(),
                            "serviceId", serviceSummary.serviceId(),
                            "createdAt", serviceSummary.createdAt(),
                            "updatedAt", serviceSummary.updatedAt(),
                            "status", serviceSummary.statusAsString()
                    )
            );
        }
        final ControlTable controlTable = ControlTable.builder()
                .tableName(APPRUNNER_CONTAINER_PROFILE)
                .headers(HEADERS)
                .rows(rows)
                .build();

        return AppRunnerControlDto.builder()
                .controlTable(controlTable)
                .build();
    }
}
