package bluegreen.service.opsworks;

import bluegreen.model.ControlTable;
import bluegreen.model.OpsWorksStacksControlDto;
import bluegreen.service.WatchAwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;
import software.amazon.awssdk.services.opsworks.OpsWorksClient;
import software.amazon.awssdk.services.opsworks.model.Stack;
import software.amazon.awssdk.services.opsworks.model.*;

import java.util.*;
import java.util.stream.Collectors;

import static bluegreen.conf.opsworks.OpsWorksStacksConfiguration.OPSWORKS_CLIENT;
import static bluegreen.conf.opsworks.OpsWorksStacksConfiguration.OPSWORKS_STACKS_CLIENT;
import static bluegreen.model.Constant.OPSWORKS_STACKS_PROFILE;

@Service
@Profile(OPSWORKS_STACKS_PROFILE)
@Slf4j
public class WatchOpsWorksStacksService implements WatchAwsService<OpsWorksStacksControlDto> {

    @Autowired
    @Qualifier(OPSWORKS_STACKS_CLIENT)
    private OpsWorksClient opsWorksStacksClient;

    @Autowired
    @Qualifier(OPSWORKS_CLIENT)
    private OpsWorksClient opsWorksClient;

    @Autowired
    private EC2MetadataUtils.InstanceInfo instanceInfo;

    public static final String STACK_NAME = "aws-codepipeline-boot";
    public static final List<String> HEADERS = List.of(
            "#", "InstanceId", "InstanceState", "CreatedAt",
            "LifeCycleEvent", "Completed", "CompletedAt");

    @Override
    public OpsWorksStacksControlDto watch() {
        final List<Instance> instances = this.getStackId()
                .map(this::getLayers)
                .stream()
                .flatMap(Collection::stream)
                .map(this::getInstances)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        final ControlTable controlTable = ControlTable.builder()
                .headers(HEADERS)
                .rows(this.getRows(instances))
                .build();
        return OpsWorksStacksControlDto.builder()
                .controlTable(controlTable)
                .instanceInfo(this.instanceInfo)
                .build();
    }

    private Optional<String> getStackId() {
        try {
            final DescribeStacksRequest request = DescribeStacksRequest.builder()
                    .build();
            final DescribeStacksResponse describeStacksResponse = this.opsWorksStacksClient.describeStacks(request);
            return describeStacksResponse.stacks().stream()
                    .filter(response -> STACK_NAME.equals(response.name()))
                    .map(Stack::stackId)
                    .findFirst();
        } catch (final Exception exception) {
            log.error("Error while retrieving stack info", exception);
            return Optional.empty();
        }
    }

    private List<Layer> getLayers(final String stackId) {
        final DescribeLayersRequest describeLayersRequest = DescribeLayersRequest.builder()
                .stackId(stackId)
                .build();
        final DescribeLayersResponse response = this.opsWorksStacksClient.describeLayers(describeLayersRequest);
        return response.layers();
    }

    private List<Instance> getInstances(final Layer layer) {
        final DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                .layerId(layer.layerId())
                .build();
        final DescribeInstancesResponse response = this.opsWorksStacksClient.describeInstances(request);
        return response.instances();
    }


    private List<Map<String, Object>> getRows(final List<Instance> instances) {
        return instances.stream()
                .map(this::getRow)
                .collect(Collectors.toList());
    }

    private Map<String, Object> getRow(final Instance instance) {
        final DescribeCommandsRequest request = DescribeCommandsRequest.builder()
                .instanceId(instance.instanceId())
                .build();
        final DescribeCommandsResponse response = this.opsWorksStacksClient.describeCommands(request);
        final Optional<Command> command = response.commands()
                .stream()
                .filter(cmd -> Objects.nonNull(cmd.createdAt()))
                .sorted(Comparator.comparing(Command::createdAt).reversed())
                .findFirst();
        return Map.of(
                "#", this.isSame(instance),
                "InstanceId", instance.ec2InstanceId(),
                "InstanceState", instance.status(),
                "CreatedAt", instance.createdAt(),
                "LifeCycleEvent", command.map(Command::type).orElse(""),
                "Completed", command.map(Command::status).orElse(""),
                "CompletedAt", command.map(Command::completedAt).orElse("")
        );
    }

    private String isSame(final Instance instance) {
        final String localInstanceId = this.instanceInfo.getInstanceId();
        final String ec2InstanceId = instance.ec2InstanceId();
        return localInstanceId.equals(ec2InstanceId) ? "X" : "";
    }
}
