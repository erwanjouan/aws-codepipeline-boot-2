package bluegreen.service.ecs;

import bluegreen.model.ControlTable;
import bluegreen.model.EcsFargateAlbControlDto;
import bluegreen.service.WatchAwsService;
import com.sun.management.OperatingSystemMXBean;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.DeploymentConfiguration;
import software.amazon.awssdk.services.ecs.model.DeploymentController;
import software.amazon.awssdk.services.ecs.model.DescribeServicesRequest;
import software.amazon.awssdk.services.ecs.model.DescribeServicesResponse;
import software.amazon.awssdk.services.ecs.model.DescribeTasksRequest;
import software.amazon.awssdk.services.ecs.model.DescribeTasksResponse;
import software.amazon.awssdk.services.ecs.model.ListTasksRequest;
import software.amazon.awssdk.services.ecs.model.ListTasksResponse;
import software.amazon.awssdk.services.ecs.model.ServiceEvent;
import software.amazon.awssdk.services.ecs.model.Task;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static bluegreen.model.Constant.ALB_ECS_EC2_PROFILE;
import static bluegreen.model.Constant.ALB_ECS_FARGATE_PROFILE;
import static bluegreen.model.Constant.AWS_DEFAULT_REGION_ENV;
import static bluegreen.model.Constant.BLUE_GREEN_ECS_FARGATE_PROFILE;
import static bluegreen.model.Constant.DISASTER_RECOVERY_PROFILE;
import static bluegreen.model.Constant.ID;
import static bluegreen.model.Constant.PROJECT_DEPLOYMENT_NAME_ENV;
import static bluegreen.model.Constant.PR_ECS_FARGATE_PROFILE;

@Service
@Profile({ALB_ECS_FARGATE_PROFILE, DISASTER_RECOVERY_PROFILE,
        PR_ECS_FARGATE_PROFILE, BLUE_GREEN_ECS_FARGATE_PROFILE,
        ALB_ECS_EC2_PROFILE})
public class WatchEcsFargateAlbService implements WatchAwsService<EcsFargateAlbControlDto> {

    @Autowired
    private EcsClient ecsClient;

    @Autowired
    private EcsTaskMetaDataService ecsTaskMetaDataService;

    @Autowired
    private OperatingSystemMXBean operatingSystemMXBean;

    @Value(PROJECT_DEPLOYMENT_NAME_ENV)
    private String projectDeploymentName;

    @Value(AWS_DEFAULT_REGION_ENV)
    private String region;

    public static final List<String> HEADERS = List.of("isCurrent", ID,
            "taskDefinition", "createdAt", "capacityProvider", "lastStatus");

    @Override
    public EcsFargateAlbControlDto watch() {
        final List<Map<String, Object>> rows = new ArrayList<>();

        final ListTasksResponse listTasksResponse = this.getListTasksResponse();

        for (final String taskArn : listTasksResponse.taskArns()) {
            final DescribeTasksResponse describeTasksResponse = this.getDescribeTasksResponse(taskArn);
            for (final Task task : describeTasksResponse.tasks()) {
                final String taskId = taskArn.split("/")[2];
                final String taskDefinition = task.taskDefinitionArn().split("/")[1];
                String createdAt = getCreated(task);
                rows.add(
                        Map.of("isCurrent", this.isFrom(taskId),
                                ID, taskId,
                                "taskDefinition", taskDefinition,
                                "createdAt", this.safe(createdAt),
                                "capacityProvider", this.safe(task::capacityProviderName),
                                "lastStatus", this.safe(task::lastStatus)
                        ));
            }
        }

        final ControlTable controlTable = this.getControlTable(rows);

        final DescribeServicesResponse describeServicesResponse = this.getDescribeServicesResponse();

        return EcsFargateAlbControlDto.builder()
                .controlTable(controlTable)
                .taskArn(this.taskArn())
                .region(this.region)
                .deploymentConfiguration(this.getDeploymentConfiguration(describeServicesResponse))
                .deploymentController(this.getDeploymentDeploymentController(describeServicesResponse))
                .cpu(this.getCpu())
                .events(this.getRecentEvents(describeServicesResponse))
                .build();
    }

    private static @NonNull String getCreated(Task task) {
        return Optional.of(task.createdAt())
                .map(instant -> LocalTime.ofInstant(task.createdAt(), ZoneId.of("Europe/Paris")))
                .map(localTime -> localTime.format(DateTimeFormatter.ISO_LOCAL_TIME))
                .orElse("unknown");
    }

    private Integer getCpu() {
        final double cpuPercent = this.operatingSystemMXBean.getCpuLoad() * 100;
        return (int) cpuPercent;
    }


    private Boolean isFrom(final String rowTaskId) {
        final String calledInstanceId = this.taskId();
        return Optional.ofNullable(calledInstanceId)
                .filter(calledId -> calledId.equals(rowTaskId))
                .isPresent();
    }

    private DescribeTasksResponse getDescribeTasksResponse(final String taskArn) {
        final DescribeTasksRequest describeTasksRequest = DescribeTasksRequest.builder()
                .cluster(this.projectDeploymentName)
                .tasks(taskArn)
                .build();
        return this.ecsClient.describeTasks(describeTasksRequest);
    }

    private String taskId() {
        return this.ecsTaskMetaDataService.getTaskId();
    }

    private String taskArn() {
        return this.ecsTaskMetaDataService.getTaskArn();
    }

    private String getStats() {
        return this.ecsTaskMetaDataService.stats();
    }

    private ListTasksResponse getListTasksResponse() {
        final ListTasksRequest request = ListTasksRequest.builder()
                .cluster(this.projectDeploymentName)
                .serviceName(this.projectDeploymentName)
                .build();
        return this.ecsClient.listTasks(request);
    }

    private DescribeServicesResponse getDescribeServicesResponse() {
        final DescribeServicesRequest describeServicesRequest = DescribeServicesRequest.builder()
                .cluster(this.projectDeploymentName)
                .services(this.projectDeploymentName)
                .build();
        return this.ecsClient.describeServices(describeServicesRequest);
    }

    private DeploymentController getDeploymentDeploymentController(final DescribeServicesResponse describeServicesResponse) {
        return describeServicesResponse.services().stream()
                .map(software.amazon.awssdk.services.ecs.model.Service::deploymentController)
                .findFirst()
                .orElse(null);
    }

    private DeploymentConfiguration getDeploymentConfiguration(final DescribeServicesResponse describeServicesResponse) {
        return describeServicesResponse.services().stream()
                .map(software.amazon.awssdk.services.ecs.model.Service::deploymentConfiguration)
                .findFirst()
                .orElse(null);
    }

    private static final DateTimeFormatter EVENT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Europe/Paris"));

    private List<String> getRecentEvents(final DescribeServicesResponse describeServicesResponse) {
        return describeServicesResponse.services().stream()
                .findFirst()
                .map(svc -> svc.events().stream()
                        .limit(5)
                        .map(e -> EVENT_FMT.format(e.createdAt()) + " – " + e.message())
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    private ControlTable getControlTable(final List<Map<String, Object>> rows) {
        return ControlTable.builder()
                .tableName(ALB_ECS_FARGATE_PROFILE)
                .headers(HEADERS)
                .rows(rows)
                .build();
    }
}
