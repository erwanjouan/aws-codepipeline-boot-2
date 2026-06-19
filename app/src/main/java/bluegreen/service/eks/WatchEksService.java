package bluegreen.service.eks;

import bluegreen.model.ControlTable;
import bluegreen.model.EksControlDto;
import bluegreen.service.WatchAwsService;
import com.sun.management.OperatingSystemMXBean;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerStatus;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static bluegreen.model.Constant.AWS_DEFAULT_REGION_ENV;
import static bluegreen.model.Constant.EKS_FARGATE_PROFILE;
import static bluegreen.model.Constant.EKS_HOSTNAME;
import static bluegreen.model.Constant.PROJECT_DEPLOYMENT_NAME_ENV;

@Service
@Profile({EKS_FARGATE_PROFILE})
public class WatchEksService implements WatchAwsService<EksControlDto> {

    @Autowired
    private CoreV1Api api;

    @Value(PROJECT_DEPLOYMENT_NAME_ENV)
    private String projectDeploymentName;

    @Value(AWS_DEFAULT_REGION_ENV)
    private String region;

    @Value(EKS_HOSTNAME)
    private String podName;

    @Autowired
    private OperatingSystemMXBean operatingSystemMXBean;

    public static final List<String> HEADERS = List.of("#", "podIp", "imageTag", "podName", "status", "startTime",
            "restartCount");

    @Override
    public EksControlDto watch() {
        final List<Map<String, Object>> rows = new ArrayList<>();
        final String appLabel = "app-" + this.projectDeploymentName;
        final String labelSelector = "app.kubernetes.io/name=" + appLabel;
        try {
            this.api.listNamespacedPod(this.projectDeploymentName, null, null, null, null,
                            labelSelector, null, null, null, 10, false)
                    .getItems()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(v1Pod -> Objects.nonNull(v1Pod.getStatus()))
                    .filter(v1Pod -> Objects.nonNull(v1Pod.getMetadata()))
                    .forEach(v1Pod -> {
                        final V1PodStatus status = v1Pod.getStatus();
                        rows.add(Map.of(
                                "#", this.isFrom(v1Pod.getMetadata().getName()),
                                "podIp", this.safe(status::getPodIP),
                                "imageTag", this.getImageTag(v1Pod),
                                "status", this.safe(status::getPhase),
                                "startTime", this.safe(status::getStartTime),
                                "restartCount", this.getRestartCount(status),
                                "podName", this.safe(v1Pod.getMetadata()::getName)
                        ));
                    });
        } catch (final ApiException e) {
            e.printStackTrace();
        }

        final ControlTable controlTable = this.getControlTable(rows);

        return EksControlDto.builder()
                .controlTable(controlTable)
                .region(this.region)
                .podName(this.podName)
                .cpu(this.getCpu())
                .build();
    }

    private String getImageTag(final io.kubernetes.client.openapi.models.V1Pod v1Pod) {
        return Optional.ofNullable(v1Pod)
                .map(V1Pod::getSpec)
                .map(V1PodSpec::getContainers)
                .filter(v1Containers -> !v1Containers.isEmpty())
                .map(v1Containers -> v1Containers.get(0))
                .map(V1Container::getImage)
                .map(image -> image.split(":")[1])
                .map(tag -> tag.substring(0, 8))
                .orElse("");
    }

    private Integer getRestartCount(final V1PodStatus status) {
        return Optional.ofNullable(status)
                .map(V1PodStatus::getContainerStatuses)
                .filter(v1ContainerStatuses -> !v1ContainerStatuses.isEmpty())
                .map(v1ContainerStatuses -> v1ContainerStatuses.get(0))
                .map(V1ContainerStatus::getRestartCount)
                .orElse(0);
    }

    private String isFrom(final String hostName) {
        final boolean hasHostName = Objects.nonNull(this.podName);
        return hasHostName && this.podName.equals(hostName) ? "X" : "";
    }

    private ControlTable getControlTable(final List<Map<String, Object>> rows) {
        return ControlTable.builder()
                .tableName(EKS_FARGATE_PROFILE)
                .headers(HEADERS)
                .rows(rows)
                .build();
    }

    private Integer getCpu() {
        final double cpuPercent = this.operatingSystemMXBean.getCpuLoad() * 100;
        return (int) cpuPercent;
    }
}
