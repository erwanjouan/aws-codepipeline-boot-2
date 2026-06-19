# asg-update-policy

If no UpdatePolicy is set up in AWS::AutoScaling::AutoScalingGroup, instance will try to update itself (via Cfn-hup) but
without any coordination.

Rolling update
https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-attribute-updatepolicy.html#cfn-attributes-updatepolicy-rollingupdate

- Launch template is named with Git Sha-1, so replaced at each deployment
    - Uses cfn-init to
        - retrieve artifact from CodeArtifact
        - launches application jar in background
        - wait 20s
        - send cfn-signal
- AutoScaling Group has an
    - UpdatePolicy property set
        - AutoScalingRollingUpdate to specify rolling updates parameters

## References

https://repost.aws/knowledge-center/auto-scaling-group-rolling-updates

### Issue with cfn signal

https://repost.aws/knowledge-center/create-complete-bootstrapping

### launching spring boot app

https://springhow.com/start-stop-scripts-for-spring-boot-applications/
