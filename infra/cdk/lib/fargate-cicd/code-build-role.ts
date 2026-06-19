import { Effect, ManagedPolicy, PolicyStatement, Role, ServicePrincipal } from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';
import { ArtifactKmsKey } from './artifact-kms-key';

export class CodeBuildRole extends Construct {
    role: Role;

    constructor(scope: Construct, id: string, kmsKey: ArtifactKmsKey) {
        super(scope, id);

        const policy = new ManagedPolicy(this, 'Policy', {
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        'logs:CreateLogGroup',
                        'logs:CreateLogStream',
                        'logs:PutLogEvents',
                        'logs:PutRetentionPolicy',
                    ],
                    resources: ['arn:aws:logs:*'],
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: ['codecommit:GitPull'],
                    resources: ['*'],
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        'ecr:GetDownloadUrlForLayer',
                        'ecr:BatchGetImage',
                        'ecr:BatchCheckLayerAvailability',
                        'ecr:PutImage',
                        'ecr:InitiateLayerUpload',
                        'ecr:UploadLayerPart',
                        'ecr:CompleteLayerUpload',
                        'ecr:GetAuthorizationToken',
                    ],
                    resources: ['*'],
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        'codebuild:CreateReportGroup',
                        'codebuild:CreateReport',
                        'codebuild:UpdateReport',
                        'codebuild:BatchPutTestCases',
                    ],
                    resources: ['*'],
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
                    resources: [kmsKey.key.keyArn],
                }),
            ],
        });

        this.role = new Role(this, 'Role', {
            roleName: 'fargate-codebuild-service-role',
            assumedBy: new ServicePrincipal('codebuild.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName('AmazonS3FullAccess'),
                policy,
            ],
        });
    }
}