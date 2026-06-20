import { ManagedPolicy, Role, ServicePrincipal } from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';

export class TaskExecutionRole extends Construct {
    role: Role;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        // AmazonECSTaskExecutionRolePolicy already grants ecr:GetAuthorizationToken on *
        // and ECR pull actions on *, covering cross-account ECR image pulls when the
        // source ECR repository policy also allows this account.
        this.role = new Role(this, 'Role', {
            roleName: 'fargate-task-execution-role',
            assumedBy: new ServicePrincipal('ecs-tasks.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonECSTaskExecutionRolePolicy'),
            ],
        });
    }
}