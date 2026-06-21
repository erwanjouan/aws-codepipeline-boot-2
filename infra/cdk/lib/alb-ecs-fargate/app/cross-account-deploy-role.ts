import { AccountPrincipal, Effect, ManagedPolicy, PolicyStatement, Role } from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';
import { Constants } from '../../constants';

export class CrossAccountDeployRole extends Construct {
    role: Role;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        const policy = new ManagedPolicy(this, 'Policy', {
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        'ecs:DescribeServices',
                        'ecs:DescribeTaskDefinition',
                        'ecs:DescribeTasks',
                        'ecs:ListTasks',
                        'ecs:RegisterTaskDefinition',
                        'ecs:TagResource',
                        'ecs:UpdateService',
                    ],
                    resources: ['*'],
                }),
                // PassRole for any role passed to ECS (execution role + auto-generated task role)
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: ['iam:PassRole'],
                    resources: ['*'],
                    conditions: {
                        StringEqualsIfExists: {
                            'iam:PassedToService': 'ecs-tasks.amazonaws.com',
                        },
                    },
                }),
                // Read pipeline artifacts from CICD account S3 bucket
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: ['s3:GetObject', 's3:GetObjectVersion', 's3:GetBucketVersioning', 's3:ListBucket'],
                    resources: ['*'],
                }),
                // Decrypt CICD artifact bucket KMS key
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: ['kms:Decrypt', 'kms:DescribeKey'],
                    resources: ['*'],
                }),
            ],
        });

        // Trust the entire CICD account — the source-side restriction is enforced by
        // the CodePipeline role's identity policy, which grants sts:AssumeRole only
        // for this specific cross-account role ARN. Trusting the account root avoids
        // a chicken-and-egg validation failure when the pipeline role doesn't exist yet.
        this.role = new Role(this, 'Role', {
            roleName: Constants.FARGATE_CROSS_ACCOUNT_ROLE_NAME,
            assumedBy: new AccountPrincipal(process.env.CICD_ACCOUNT_ID),
            managedPolicies: [policy],
        });
    }
}