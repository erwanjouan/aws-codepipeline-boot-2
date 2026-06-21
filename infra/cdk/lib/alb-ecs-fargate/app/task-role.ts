import { Effect, PolicyStatement, Role, ServicePrincipal } from 'aws-cdk-lib/aws-iam';
import { Stack } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { Constants } from '../../constants';

export class TaskRole extends Construct {
    role: Role;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        const { account, region } = Stack.of(this);

        this.role = new Role(this, 'Role', {
            roleName: 'fargate-task-role',
            assumedBy: new ServicePrincipal('ecs-tasks.amazonaws.com'),
        });

        this.role.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            actions: ['ecs:DescribeTasks', 'ecs:ListTasks'],
            resources: ['*'],
        }));

        this.role.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            actions: ['s3:*'],
            resources: [
                `arn:aws:s3:::${process.env.PROJECT_NAME}`,
                `arn:aws:s3:::${process.env.PROJECT_NAME}/*`,
            ],
        }));

        this.role.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            actions: ['ecs:ListServices', 'ecs:DescribeServices'],
            resources: [
                `arn:aws:ecs:${region}:${account}:service/${process.env.PROJECT_DEPLOYMENT_NAME}/${process.env.PROJECT_DEPLOYMENT_NAME}`,
            ],
        }));

        this.role.addToPolicy(new PolicyStatement({
            effect: Effect.ALLOW,
            actions: ['ssm:DescribeParameters', 'ssm:GetParameter', 'ssm:GetParameters'],
            resources: ['*'],
        }));
    }
}
