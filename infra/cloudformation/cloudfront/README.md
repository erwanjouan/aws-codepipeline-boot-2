# CloudFront

CloudFront distribution that has a Custom origin linked to a Load balancer and auto scaling group.

Cloudformation replaces the auto scaling group entirely with a new one at each update, as described in :
https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-attribute-updatepolicy.html#cfn-attributes-updatepolicy-replacingupdate

If no UpdatePolicy is set up in AWS::AutoScaling::AutoScalingGroup, instance will try to update itself (via Cfn-hup) but
without any coordination.

## References

https://repost.aws/knowledge-center/auto-scaling-group-rolling-updates

### launching spring boot app

https://springhow.com/start-stop-scripts-for-spring-boot-applications/
