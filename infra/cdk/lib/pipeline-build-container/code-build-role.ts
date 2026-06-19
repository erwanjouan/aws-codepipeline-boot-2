import { Construct } from "constructs";
import { Effect, ManagedPolicy, PolicyStatement, Role, ServicePrincipal } from "aws-cdk-lib/aws-iam";

export class CodeBuildRole extends Construct {
    
    role:Role

    constructor(scope: Construct, id: string) {
        super(scope, id);
        const codeBuildPolicy = new ManagedPolicy(this, 'CodeBuildPolicy', {
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        "logs:CreateLogGroup",
                        "logs:CreateLogStream",
                        "logs:DescribeLogStreams",
                        "logs:GetLogEvents",
                        "logs:PutLogEvents",
                        "logs:PutRetentionPolicy"
                    ],
                    resources: ["arn:aws:logs:*"]
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        "codecommit:GitPull"
                    ],
                    resources: ["*"]
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        "cloudformation:CreateStack"
                    ],
                    resources: ["*"]
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        "codebuild:CreateReportGroup",
                        "codebuild:CreateReport",
                        "codebuild:UpdateReport",
                        "codebuild:BatchPutTestCases"
                    ],
                    resources: [`arn:aws:codebuild:${process.env.CDK_DEFAULT_REGION}:${process.env.CDK_DEFAULT_ACCOUNT}:report-group/*`]
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        "kms:Decrypt",
                        "kms:DecryptKey",
                        "kms:Encrypt",
                        "kms:ReEncrypt*",
                        "kms:GenerateDataKey*"
                    ],
                    resources: ["*"]
                })
            ]
        })

        const codeBuildArtifactPolicy = new ManagedPolicy(this, 'CodeBuildArtifactPolicy', {
            statements: [
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        "ecr:GetDownloadUrlForLayer",
                        "ecr:BatchGetImage",
                        "ecr:BatchCheckLayerAvailability",
                        "ecr:PutImage",
                        "ecr:InitiateLayerUpload",
                        "ecr:UploadLayerPart",
                        "ecr:CompleteLayerUpload",
                        "ecr:GetAuthorizationToken",
                    ],
                    resources: ['*']
                }),
                new PolicyStatement({
                    effect: Effect.ALLOW,
                    actions: [
                        "ssm:DescribeParameters",
                        "ssm:GetParameters",
                        "ssm:GetParameter"
                    ],
                    resources: ['*']
                })
            ]
        })

        const codeBuildServiceRole = new Role(this, 'CodeBuildServiceRole', {
            roleName: 'CodeBuildServiceRole',
            assumedBy:  new ServicePrincipal('codebuild.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName('AmazonS3FullAccess'),
                codeBuildPolicy,
                codeBuildArtifactPolicy
            ]
        })

        this.role = codeBuildServiceRole

    }
}