# asg-codedeploy-ec2

- Notes
    - BlueGreen on CodeDeploy should be configured in Management Console
      because [CloudFormation does not support EC2 BLUE_GREEN deployment.](https://repost.aws/questions/QUenZOjcB7Q7GEQOhtzm6G5A/how-do-you-define-copy-auto-scaling-group-for-codedeploy-deploymentgroup-in-cloudformation#ANBd6H4RcJTyaEsQmYOd8sdg)

- [Create a deployment group for an EC2/On-Premises blue/green deployment in CodeDeploy Console](https://docs.aws.amazon.com/codedeploy/latest/userguide/deployment-groups-create-blue-green.html)
- [Required permission for AWS Blue/Green Deployments with Launch Templates](https://h2ik.co/2019/02/28/aws-codedeploy-blue-green/)
- [Integrating CodeDeploy with Amazon EC2 Auto Scaling](https://docs.aws.amazon.com/codedeploy/latest/userguide/integrations-aws-auto-scaling.html)

## Cfn init

- [CloudFormation cfn-init pitfall: Auto scaling and throttling error rate exceeded](https://cloudonaut.io/cloudformation-cfn-init-pitfall-throttling-error-rate-exceeded/)

## Default configuration

- CodeDeploy Deployment Group is configured with:
    - DeploymentType : IN_PLACE
    - DeploymentOption : WITHOUT_TRAFFIC_CONTROL
    - EC2 tags for registering CodeDeploy instance.

This is only one supported currently in CloudFormation.

## Manual actions required.

As setting ALB, TargetGRoup for CodeDeploy DeploymentGroup (making BlueGreen deployment) is not supported in out-of-the
box in CloudFormation, a complementary configuration should be done in CodeDeploy Console. Target Groups and Load
balancers should be set manually in CodeDeploy console.