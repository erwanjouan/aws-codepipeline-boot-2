import { Effect, IRole, ManagedPolicy, PolicyDocument, PolicyStatement, Role, ServicePrincipal } from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';
import { Constants } from '../constants';

export class CodePipelineRole extends Construct {
    role: IRole;

    constructor(scope: Construct, id: string) {
        super(scope, id);

        const policy = new ManagedPolicy(this, 'Policy', {
            managedPolicyName: 'fargate-codepipeline-policy',
            document: new PolicyDocument({
                statements: [
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        actions: ['codebuild:StartBuild', 'codebuild:BatchGetBuilds', 'iam:PassRole'],
                        resources: ['*'],
                    }),
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        actions: ['codestar-connections:UseConnection'],
                        resources: ['*'],
                    }),
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        actions: [
                            'ecs:DescribeServices',
                            'ecs:DescribeTaskDefinition',
                            'ecs:RegisterTaskDefinition',
                            'ecs:UpdateService',
                            'ecs:ListTasks',
                            'ecs:DescribeTasks',
                            'ecs:TagResource',
                        ],
                        resources: ['*'],
                    }),
                    // Assume cross-account role in PROD to deploy to ECS
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        actions: ['sts:AssumeRole'],
                        resources: [
                            `arn:aws:iam::${Constants.WORKLOAD_ACCOUNT_ID}:role/${Constants.FARGATE_CROSS_ACCOUNT_ROLE_NAME}`,
                        ],
                    }),
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        actions: [
                            'kms:Decrypt',
                            'kms:Encrypt',
                            'kms:ReEncrypt*',
                            'kms:GenerateDataKey*',
                            'kms:DescribeKey',
                        ],
                        resources: ['*'],
                    }),
                ],
            }),
        });

        this.role = new Role(this, 'Role', {
            roleName: 'fargate-codepipeline-role',
            assumedBy: new ServicePrincipal('codepipeline.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName('AmazonS3FullAccess'),
                policy,
            ],
        });
    }
}