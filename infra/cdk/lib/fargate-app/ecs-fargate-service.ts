import { Cluster, ContainerImage, FargateService } from 'aws-cdk-lib/aws-ecs';
import { ApplicationLoadBalancedFargateService } from 'aws-cdk-lib/aws-ecs-patterns';
import { Vpc } from 'aws-cdk-lib/aws-ec2';
import { Construct } from 'constructs';
import { Constants } from '../constants';
import { TaskExecutionRole } from './task-execution-role';

export class EcsFargateService extends Construct {
    service: FargateService;

    constructor(scope: Construct, id: string, taskExecutionRole: TaskExecutionRole) {
        super(scope, id);

        const vpc = new Vpc(this, 'Vpc', { maxAzs: 2 });

        const cluster = new Cluster(this, 'Cluster', {
            clusterName: Constants.FARGATE_CLUSTER_NAME,
            vpc,
        });

        // Placeholder image; the pipeline replaces it on first run via imagedefinitions.json.
        // The container name must match FARGATE_CONTAINER_NAME so ECS deploy action
        // can locate it in the task definition.
        // nginx:alpine is configured via command override to serve HTTP 200 on /actuator/health
        // so the ALB health check passes before the real app is deployed.
        const albService = new ApplicationLoadBalancedFargateService(this, 'Service', {
            cluster,
            serviceName: Constants.FARGATE_SERVICE_NAME,
            memoryLimitMiB: 2048,
            desiredCount: 1,
            cpu: 1024,
            taskImageOptions: {
                image: ContainerImage.fromRegistry('nginx:alpine'),
                containerName: Constants.FARGATE_CONTAINER_NAME,
                executionRole: taskExecutionRole.role,
                containerPort: 8080,
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