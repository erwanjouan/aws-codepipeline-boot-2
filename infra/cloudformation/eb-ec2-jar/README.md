# ElasticBeanStalk on Ec2

CodePipeline with Elastic Beanstalk deployment provider.

## ElasticBeanStalk Configuration

### ElasticBeanStalk Configuration Methods

- Direct settings in environment
    - AWS Management Console, EB Cli, AWS Cli, Sdk
- [.ebextensions](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/ebextensions.html)
    - At the root of application bundle
    - Files should have ".config" extension
    - Supportds Json or Yaml formatting
    - Several sections present
- [saved_configurations](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/environment-configuration-savedconfig.html) (
  via "configuration template")
    - Yaml formatted
    - Can be retrieved from current environment
    - By default, saved configurations are stored in the Elastic Beanstalk S3 bucket in a folder named after your
      application. For example, configurations for an application named my-app in the us-west-2 region for account
      number 123456789012 can be found at s3://elasticbeanstalk-us-west-2-123456789012/resources/templates/my-app.
    - Can be stored in custom S3 bucket
    - Are linked to applications (not environment)
    - Each property from saved config can be removed individually (reset to values in downstream declarations)
        - see "reset-config" target in Makefile
- [Environment manifest env.yaml](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/environment-cfg-manifest.html)

cf [Configuring Elastic Beanstalk environments](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/beanstalk-environment-configuration-advanced.html)

### Configuration Precedence

- (highest) Direct settings in environment
    - Management console
    - CloudFormation template
- (lower) Saved configuration
    - EB Cli
    - Aws Cli
- (lower) Configuration files in .ebextensions
- (lowest) Default values

## Build and Deployment

### Platform hook

[Extending Elastic Beanstalk Linux platforms](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/platforms-linux-extend.html)

Application can be built by platform Hook, as described in Buildfile.

### Deployment policy

[Deployment policy](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.rolling-version-deploy.html?icmpid=docs_elasticbeanstalk_console)

Update
the [DeploymentPolicy](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.rolling-version-deploy.html)
option in Cloudformation deploy Stack to set the deployment type. The following values are supported:

- AllAtOnce
    - Disables rolling deployments and always deploys to all instances simultaneously.
- Rolling
    - Enables standard rolling deployments.
- RollingWithAdditionalBatch
    - Launches an extra batch of instances, before starting the deployment, to maintain full capacity.
- Immutable
    - Performs
      an [immutable update](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/environmentmgmt-updates-immutable.html)
      for every deployment.
- TrafficSplitting
    - Deploy the new version to a fresh group of instances and temporarily split incoming client traffic between the
      existing application version and the new one.