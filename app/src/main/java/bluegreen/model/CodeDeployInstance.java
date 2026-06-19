package bluegreen.model;

import software.amazon.awssdk.services.codedeploy.model.TargetStatus;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceState;
import software.amazon.awssdk.services.ec2.model.InstanceType;

import java.time.Instant;

public class CodeDeployInstance {

    private final Instance instance;
    private TargetStatus targetStatus;
    private Instant lastUpdated;
    private String deploymentId;
    private String lifecycleEventName;

    public CodeDeployInstance(final Instance instance) {
        this.instance = instance;
    }

    public String instanceId() {
        return this.instance.instanceId();
    }

    public String imageId() {
        return this.instance.imageId();
    }

    public InstanceType instanceType() {
        return this.instance.instanceType();
    }

    public InstanceState state() {
        return this.instance.state();
    }

    public TargetStatus targetStatus() {
        return this.targetStatus;
    }

    public void setTargetStatus(final TargetStatus targetStatus) {
        this.targetStatus = targetStatus;
    }

    public Instant lastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(final Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String deploymentId() {
        return this.deploymentId;
    }

    public void setDeploymentId(final String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String lifecycleEventName() {
        return this.lifecycleEventName;
    }

    public void setLifecycleEventName(final String lifecycleEventName) {
        this.lifecycleEventName = lifecycleEventName;
    }
}
