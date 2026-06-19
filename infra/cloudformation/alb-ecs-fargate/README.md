# alb-ecs-fargate

- Capacity provider
    - Fargate
    - Fargate Spot

- Stress application
    - /custom/stress (true or false)

- Uninstall may need to be relaunched due to ```capacity planning association```

## Update

- Just push new version to source control

## Container Image scanning

- Manual approval stage: ImageScanStage
- Codepipeline stage sends a message to SNS topic
    - A lambda subscribes to the topic and starts ECR basic scanning
- EventBridge rule listens to Image scan completion
    - Triggers another lambda that
        - retrieves manual approval stage token
        - updates codepipeline with scan status

## Service deployment with ECS Controller (100-200%)

- New task will be spawned next to current one.
- No service disruption with this configuration
- But during few seconds both old and new versions are served.
