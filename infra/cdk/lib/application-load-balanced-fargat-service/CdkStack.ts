import * as cdk from "aws-cdk-lib";
import {Construct} from "constructs";
import {CustomStackProps} from "../utils/custom-stack-props";
import {ApplicationLoadBalancedFargateService} from "aws-cdk-lib/aws-ecs-patterns";
import {Cluster, ContainerImage} from "aws-cdk-lib/aws-ecs";
import {Constants} from "../constants";
import {Repository} from "aws-cdk-lib/aws-ecr";
import {ApplicationProtocol} from "aws-cdk-lib/aws-elasticloadbalancingv2";

// too much opi
export class ApplicationLoadBalancedFargateServiceStack extends cdk.Stack {

    constructor(scope: Construct, id: string, props?: CustomStackProps) {

        super(scope, id, props);

        const cluster = new Cluster(this, 'cluster');

        const applicationPort: number = 8080;
        const deploymentName:string = 'alb-ecs-fargate'
        const projectDeploymentName:string = `${Constants.PROJECT_NAME}-${deploymentName}`

        let repositoryName = 'aws-codepipeline-boot-pipeline-build-container';
        const repository = Repository.fromRepositoryName(this, 'MyRepository', repositoryName);
        let ecrImage = ContainerImage.fromEcrRepository(repository, 'latest');

        const loadBalancedFargateService = new ApplicationLoadBalancedFargateService(this, 'Service', {
            cluster,
            memoryLimitMiB: 2048,
            desiredCount: 1,
            cpu: 1024,
            taskImageOptions: {
                image: ecrImage,
                environment: {
                    SERVER_PORT: '80',
                    SPRING_PROFILES_ACTIVE: 'alb-ecs-fargate',
                    PROJECT_DEPLOYMENT_NAME: projectDeploymentName,
                    AWS_DEFAULT_REGION: process.env.CDK_DEFAULT_REGION!
                }
            },
        });

    }
}