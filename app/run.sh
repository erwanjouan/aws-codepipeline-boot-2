 #!/bin/sh
SPRING_PROFILES_ACTIVE=alb-ecs-fargate \
AWS_DEFAULT_REGION=eu-west-3 \
PROJECT_NAME=aws-codepipeline-boot \
DEPLOYMENT_NAME=alb-ecs-fargate \
PROJECT_DEPLOYMENT_NAME=alb-ecs-fargate \
java -jar target/aws-codepipeline-boot-0.0.1-SNAPSHOT.jar