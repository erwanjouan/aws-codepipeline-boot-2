# Api Gateway Lambda proxy mode with aliases

Sample deployment on Api-Gateway with Lambda proxy integration.
Base deployment is done in CodePipeline, whereas version switching is manual in CodeDeploy.

Proxy method in Api Gateway aims at Lambda:alias, where alias is initialized by default to the first deployed version of
Lambda code.

## Update

### Publish New version with CodeDeploy

API Gateway points to a Lambda function via a stage variable called "LambdaAlias", whose value is set to "alias" by
default. This "alias" value is the name of the alias that points to the applicable Lambda version.
Pushing code has no effect on the alias but only updates the $LATEST version.

To refresh the deployed Lambda version:

- After the code is being uploaded to $LATEST,
- Publish a new version of Lambda in Management Console
- Invoke the following target in Makefile

```shell
make deploy
```

- This will map the alias

### Canary deployment on API Gateway

API Gateway can direct a percentage of traffic to a new stage configuration with overriding Stage Variable.
In order to do so:

- Publish a version of Lambda in Management Console
- Create an alias linked to the Lambda version (e.g. "green")
- Invoke the following target in Makefile

```shell
make canary
```

- At the end canary version can be permanently promoted in Management Console

### Move the alias

Edit the alias to point to this new version.

## Deployment configuration

- stage = dev
- stageVariables
    - lambdaAlias = alias

## Deployment with CodeDeploy

- Deploy a first version of the lambda. Cloudformation will :
    - create the initial version (e.g. version is 15)
    - create an alias and link it to the initial version
- Modify the code and redeploy
- Go to latest (unqualified ARN) and publish a new version (e.g. version adds a 16 entry)
- In order api Gateway to serve the new version. Go to CodeDeploy;
    - Application > DeploymentGroup > Create Deployment
    - Use AppSpec editor (YAML), paste the following :

```yaml
version: 0.0
Resources:
  - myLambdaFunction:
      Type: AWS::Lambda::Function
      Properties:
        Name: aws-dev-spring-boot-jar-api-gw-lambda
        Alias: alias
        CurrentVersion: 15
        TargetVersion: 16
```

    - [Create Deployment] --> Alias "alias" will shift from Lambda version 15 to 16

TODO: Hooks..

### References

Simple Lambda proxy integration behind Api Gateway
https://gist.github.com/balintsera/7c45340ad31d1856d6cdb1fd9a09fc7c

Make a Spring Boot 2 lambda if spring-boot-starter-parent moduel is not the maven parent
https://github.com/awslabs/aws-serverless-java-container/wiki/Quick-start---Spring-Boot2

Managing lambda with aliases and versions
https://medium.com/@vallepu.sravanthi/creating-stages-in-api-gateway-adding-alias-in-lambda-465af563f0ac)

Lambda deployment with CodeDeploy
https://www.youtube.com/watch?v=mYcRPKeCPXc

AWS Lambda Execution context in Java demystified
https://blog.ippon.tech/lambda-execution-context-demystified/

Spring Boot 2 with Serverless on AWS
https://radualmasan.com/blog/spring-boot-with-serverless-on-aws
