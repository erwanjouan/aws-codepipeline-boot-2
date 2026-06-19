package bluegreen.service.eb;

import bluegreen.model.AsgInfo;
import bluegreen.model.ControlTable;
import bluegreen.model.EbControlDto;
import bluegreen.model.InstanceInfo;
import bluegreen.service.WatchAwsService;
import bluegreen.service.asg.AutoScalingGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.elasticbeanstalk.ElasticBeanstalkClient;
import software.amazon.awssdk.services.elasticbeanstalk.model.ConfigurationOptionSetting;
import software.amazon.awssdk.services.elasticbeanstalk.model.ConfigurationSettingsDescription;
import software.amazon.awssdk.services.elasticbeanstalk.model.Deployment;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeConfigurationSettingsRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeConfigurationSettingsResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeInstancesHealthRequest;
import software.amazon.awssdk.services.elasticbeanstalk.model.DescribeInstancesHealthResponse;
import software.amazon.awssdk.services.elasticbeanstalk.model.InstancesHealthAttribute;
import software.amazon.awssdk.services.elasticbeanstalk.model.SingleInstanceHealth;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static bluegreen.model.Constant.EB_EC2_JAR_PROFILE;
import static bluegreen.model.Constant.EB_MULTI_DOCKER_PROFILE;
import static bluegreen.model.Constant.EB_SINGLE_DOCKER_PROFILE;

@Service
@Profile({EB_SINGLE_DOCKER_PROFILE, EB_MULTI_DOCKER_PROFILE, EB_EC2_JAR_PROFILE})
public class WatchEbService implements WatchAwsService<EbControlDto> {

    public static final String COMMAND_NAMESPACE = "aws:elasticbeanstalk:command";
    public static final String TRAFFIC_SPLITTING_NAMESPACE = "aws:elasticbeanstalk:trafficsplitting";
    public static final String ALL_AT_ONCE = "AllAtOnce";
    public static final String DEPLOYMENT_POLICY = "DeploymentPolicy";
    public static final String IGNORE_HEALTH_CHECK = "IgnoreHealthCheck";
    public static final String TIMEOUT = "Timeout";
    public static final String ROLLING = "Rolling";
    public static final String BATCH_SIZE = "BatchSize";
    public static final String BATCH_SIZE_TYPE = "BatchSizeType";
    public static final String ROLLING_WITH_ADDITIONAL_BATCH = "RollingWithAdditionalBatch";
    public static final String TRAFFIC_SPLITTING = "TrafficSplitting";
    public static final String NEW_VERSION_PERCENT = "NewVersionPercent";
    public static final String EVALUATION_TIME = "EvaluationTime";
    public static final String DOCKER_MULTI_CONTAINER = "Docker Multi Container";
    public static final String DOCKER_SINGLE_CONTAINER = "Docker Single Container";
    public static final String EC2_JAR = "EC2 Jar";

    @Autowired
    private ElasticBeanstalkClient elasticBeanstalkClient;

    @Autowired
    private AutoScalingGroupService autoScalingGroupService;

    @Autowired
    private InstanceInfo instanceInfo;

    @Value("${PROJECT_DEPLOYMENT_NAME}")
    private String projectDeploymentName;

    @Autowired
    private Environment environment;

    public static final List<String> HEADERS = List.of(
            "#", "InstanceId",
            "InstanceState", "DeploymentId",
            "AsgName", "AsgLifeCycle",
            "DeploymentTime", "DeploymentStatus");

    @Override
    public EbControlDto watch() {
        final ControlTable controlTable = ControlTable.builder()
                .headers(HEADERS)
                .rows(this.getRows())
                .build();
        final Map<String, Object> deploymentInfo = this.getDeploymentInfo();
        return EbControlDto.builder()
                .controlTable(controlTable)
                .deploymentInfo(deploymentInfo)
                .instanceInfo(this.instanceInfo)
                .platformType(this.getPlatformType())
                .build();
    }

    private List<Map<String, Object>> getRows() {
        final DescribeInstancesHealthRequest request = DescribeInstancesHealthRequest.builder()
                .environmentName(this.getProjectDeploymentName())
                .attributeNames(InstancesHealthAttribute.ALL)
                .build();
        final DescribeInstancesHealthResponse response = this.elasticBeanstalkClient.describeInstancesHealth(request);
        final List<String> instanceIds = response.instanceHealthList().stream().map(SingleInstanceHealth::instanceId).collect(Collectors.toList());
        final Map<String, AsgInfo> instancesAsg = this.autoScalingGroupService.getInstancesAsg(instanceIds);
        return response.instanceHealthList().stream()
                .map(singleInstanceHealth -> this.getRow(singleInstanceHealth, instancesAsg))
                .collect(Collectors.toList());
    }

    private Map<String, Object> getRow(final SingleInstanceHealth singleInstanceHealth,
                                       final Map<String, AsgInfo> asgInfoMap) {
        final AsgInfo asgInfo = asgInfoMap.getOrDefault(singleInstanceHealth.instanceId(), AsgInfo.DEFAULT_ASG_INFO);
        return Map.of(
                "#", this.isFrom(singleInstanceHealth),
                "InstanceId", this.safe(singleInstanceHealth::instanceId),
                "InstanceState", this.safe(singleInstanceHealth::healthStatus),
                "DeploymentId", Optional.ofNullable(singleInstanceHealth.deployment()).map(Deployment::deploymentId).map(String::valueOf).orElse(""),
                "AsgName", this.safe(asgInfo::getAsgName),
                "AsgLifeCycle", this.safe(asgInfo::getLifecycleState),
                "DeploymentTime", Optional.ofNullable(singleInstanceHealth.deployment()).map(Deployment::deploymentTime).map(Instant::toString).orElse(""),
                "DeploymentStatus", Optional.ofNullable(singleInstanceHealth.deployment()).map(Deployment::status).orElse(""));
    }

    private String isFrom(final SingleInstanceHealth singleInstanceHealth) {
        final String rowInstanceId = singleInstanceHealth.instanceId();
        final String calledInstanceId = this.instanceInfo.getInstanceId();
        return Optional.ofNullable(calledInstanceId)
                .filter(calledId -> calledId.equals(rowInstanceId))
                .map(i -> "X")
                .orElse("");
    }

    private Map<String, Object> getDeploymentInfo() {
        final DescribeConfigurationSettingsRequest request = DescribeConfigurationSettingsRequest.builder()
                .applicationName(this.getProjectDeploymentName())
                .environmentName(this.getProjectDeploymentName())
                .build();
        final DescribeConfigurationSettingsResponse response = this.elasticBeanstalkClient.describeConfigurationSettings(request);
        final Map<String, String> commandOptions = this.describeConfigSettings(response, COMMAND_NAMESPACE);
        final Map<String, Object> toReturn = new LinkedHashMap<>();
        toReturn.put(DEPLOYMENT_POLICY, commandOptions.get(DEPLOYMENT_POLICY));
        toReturn.put(IGNORE_HEALTH_CHECK, commandOptions.get(IGNORE_HEALTH_CHECK));
        toReturn.put(TIMEOUT, commandOptions.get(TIMEOUT));
        switch (commandOptions.get(DEPLOYMENT_POLICY)) {
            case ROLLING_WITH_ADDITIONAL_BATCH:
            case ROLLING:
                toReturn.put(BATCH_SIZE, commandOptions.get(BATCH_SIZE));
                toReturn.put(BATCH_SIZE_TYPE, commandOptions.get(BATCH_SIZE_TYPE));
                return toReturn;
            case TRAFFIC_SPLITTING:
                final Map<String, String> trafficSplittingOptions = this.describeConfigSettings(response, TRAFFIC_SPLITTING_NAMESPACE);
                toReturn.put(NEW_VERSION_PERCENT, trafficSplittingOptions.get(NEW_VERSION_PERCENT));
                toReturn.put(EVALUATION_TIME, trafficSplittingOptions.get(EVALUATION_TIME));
                return toReturn;
            default:
                return toReturn;
        }
    }

    private Map<String, String> describeConfigSettings(final DescribeConfigurationSettingsResponse response,
                                                       final String namespace) {
        return response.configurationSettings()
                .stream()
                .map(ConfigurationSettingsDescription::optionSettings)
                .flatMap(Collection::stream)
                .filter(configurationOptionSetting -> namespace.equals(configurationOptionSetting.namespace()))
                .collect(Collectors.toMap(ConfigurationOptionSetting::optionName, ConfigurationOptionSetting::value));
    }

    private String getProjectDeploymentName() {
        return this.projectDeploymentName;
    }

    private String getPlatformType() {
        final String[] activeProfiles = this.environment.getActiveProfiles();
        if (Arrays.asList(activeProfiles).contains(EB_MULTI_DOCKER_PROFILE)) {
            return DOCKER_MULTI_CONTAINER;
        } else if (Arrays.asList(activeProfiles).contains(EB_SINGLE_DOCKER_PROFILE)) {
            return DOCKER_SINGLE_CONTAINER;
        } else {
            return EC2_JAR;
        }
    }
}
