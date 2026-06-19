# Elastic BeanStalk with single/multi Docker container

CodePipeline with CloudFormation deployment provider

- Set DockerPlatformType in ```cicd.yml``` to
    - ```Single``` : single docker container in EC2
        - no ECS. EC2 are in ASG.
        - Docker engine pre-install on AMI
        - Dockerrun.aws.json version 1

    - ```Multi``` : multi container in ECS
        - ECS Cluster is created (EC2 launch type)
        - ECS agent is running on each EC2 (also managed by ASG)
        - Docker engine pre-install on AMI
        - Docker-compose is possible
        - Dockerrun.aws.json version 2

- Base Parameter
    - Container = 8080
    - Host = 80

[EB Configuration options](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/command-options.html)

[EB Configuration options general](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/command-options-general.html)

[EB Deployment policies and settings](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.rolling-version-deploy.html)

Artifacts are deployed on EC2 running Docker.

- Single container:

## Deployment policies

Update
the [DeploymentPolicy](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.rolling-version-deploy.html)
option in Cloudformation deploy Stack to set the deployment type. The following values are supported:

- AllAtOnce
    - Disables rolling deployments and always deploys to all instances simultaneously.
- Rolling
    - Enables standard rolling deployments
    - If Batch size is set to 100%, deployment will be similar to AllAtOnce
- RollingWithAdditionalBatch
    - Launches an extra batch of instances, before starting the deployment, to maintain full capacity.
- Immutable
    - Performs
      an [immutable update](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/environmentmgmt-updates-immutable.html)
      for every deployment.
- TrafficSplitting
    - Deploy the new version to a fresh group of instances and temporarily split incoming client traffic between the
      existing application version and the new one.