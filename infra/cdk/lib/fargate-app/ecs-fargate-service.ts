import { RemovalPolicy } from 'aws-cdk-lib';
import { Cluster, ContainerImage, FargateService, LogDrivers } from 'aws-cdk-lib/aws-ecs';
import { ApplicationLoadBalancedFargateService } from 'aws-cdk-lib/aws-ecs-patterns';
import { Vpc } from 'aws-cdk-lib/aws-ec2';
import { LogGroup, RetentionDays } from 'aws-cdk-lib/aws-logs';
import { Construct } from 'constructs';
import { Constants } from '../constants';
import { TaskExecutionRole } from './task-execution-role';
import { TaskRole } from './task-role';

export class EcsFargateService extends Construct {
    service: FargateService;

    constructor(scope: Construct, id: string, taskExecutionRole: TaskExecutionRole, taskRole: TaskRole) {
        super(scope, id);

        const vpc = new Vpc(this, 'Vpc', { maxAzs: 2 });

        const cluster = new Cluster(this, 'Cluster', {
            clusterName: process.env.PROJECT_DEPLOYMENT_NAME,
            vpc,
            enableFargateCapacityProviders: true,
        });

        const logGroup = new LogGroup(this, 'LogGroup', {
            logGroupName: `/ecs/fargate/${process.env.PROJECT_DEPLOYMENT_NAME}`,
            retention: RetentionDays.THREE_MONTHS,
            removalPolicy: RemovalPolicy.DESTROY,
        });

        // Placeholder image; the pipeline replaces it on first run via imagedefinitions.json.
        // The container name must match FARGATE_CONTAINER_NAME so ECS deploy action
        // can locate it in the task definition.
        // nginx:alpine is configured via command override to serve HTTP 200 on /actuator/health
        // so the ALB health check passes before the real app is deployed.
        const albService = new ApplicationLoadBalancedFargateService(this, 'Service', {
            cluster,
            serviceName: process.env.PROJECT_DEPLOYMENT_NAME,
            cpu: 512,
            memoryLimitMiB: 1024,
            desiredCount: 2,
            capacityProviderStrategies: [
                { capacityProvider: 'FARGATE', base: 2, weight: 1 },
                { capacityProvider: 'FARGATE_SPOT', base: 0, weight: 4 },
            ],
            taskImageOptions: {
                image: ContainerImage.fromRegistry('nginx:alpine'),
                containerName: process.env.PROJECT_DEPLOYMENT_NAME,
                executionRole: taskExecutionRole.role,
                taskRole: taskRole.role,
                containerPort: 8080,
                environment: {
                    SPRING_PROFILES_ACTIVE: process.env.DEPLOYMENT_NAME!,
                    PROJECT_NAME: process.env.PROJECT_NAME!,
                    DEPLOYMENT_NAME: process.env.DEPLOYMENT_NAME!,
                    PROJECT_DEPLOYMENT_NAME: process.env.PROJECT_DEPLOYMENT_NAME!,
                },
                logDriver: LogDrivers.awsLogs({
                    logGroup,
                    streamPrefix: 'ecs-fargate',
                }),
                command: [
                    '/bin/sh', '-c',
                    "printf 'server{listen 8080;location /actuator/health{return 200;}}' > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'",
                ],
            },
        });

        albService.targetGroup.configureHealthCheck({
            path: '/actuator/health',
        });

        this.service = albService.service;
    }
}
