# BlueGreen deployment with ECS Fargate

[Deploy an Amazon ECS service with CodeDeploy](https://docs.aws.amazon.com/codedeploy/latest/userguide/tutorial-ecs-deployment.html)

[Create BlueGreen ECS deployment using CLI](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/create-blue-green.html)

## Perform BlueGreen with CodeDeploy

- Push new version to source control`
- Create new revision of ECS TaskDefinition
    - Clone current with no modification
- Update TaskDefinition version in [appspec.yml](appspec.yml)
    - e.g
    ```yaml
        Properties:
        TaskDefinition: "arn:aws:ecs:eu-west-1:467420073914:task-definition/aws-codepipeline-boot-bluegreen-ecs-fargate:15 ### UPDATE"
    ```
- CodeDeploy Console
    - Application: aws-codepipeline-boot-bluegreen-ecs-fargate
        - DeploymentGroup: aws-codepipeline-boot-bluegreen-ecs-fargate
            - [Create Deployment]
            - Revision type: Use AppSpec Editor
                - Paste the updated [appspec.yml](appspec.yml)
            - AppSpec Language: YAML
            - [Create Deployment]
            - Deploy...
            - [Terminate Old Instances Task Set]