package bluegreen.service;

import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceState;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetHealth;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetHealthDescription;

import java.util.Map;
import java.util.Optional;

public interface HasEc2Utils {

    default String getElbHealthCheck(final String instanceId, final Map<String, TargetHealthDescription> instancesHealthes) {
        return Optional.ofNullable(instancesHealthes.get(instanceId))
                .map(TargetHealthDescription::targetHealth)
                .map(TargetHealth::stateAsString)
                .orElse("");
    }

    default String getInstanceState(final Instance instance) {
        return Optional.ofNullable(instance).map(Instance::state).map(InstanceState::nameAsString).orElse("");
    }


}
