import { ArnPrincipal, Effect, IPrincipal, IRole, ManagedPolicy, PolicyDocument, PolicyStatement, Role, ServicePrincipal } from "aws-cdk-lib/aws-iam"
import { Construct } from "constructs"
import { Constants } from "../constants"

export class CodePipelineRole extends Construct {
    constructor(scope:Construct, id:string){
        super(scope, id)

        const codePipelineCustomPolicy = new ManagedPolicy(this, 'codePipelinePolicy', {
            managedPolicyName: 'codePipelinePolicy',
            document: new PolicyDocument({
                statements: [
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        actions: [
                            'codebuild:StartBuild',
                            'codebuild:BatchGetBuilds',
                            'iam:PassRole'
                        ],
                        resources: ['*']
                    }),
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        actions: [
                            'codedeploy:CreateDeployment',
                            'codedeploy:CreateDeploymentGroup',
                            'codedeploy:GetApplication',
                            'codedeploy:GetApplicationRevision',
                            'codedeploy:GetDeployment',
                            'codedeploy:GetDeploymentConfig',
                            'codedeploy:RegisterApplicationRevision'
                        ],
                        resources: ['*']
                    }),
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        actions: ['codecommit:*'],
                        resources: ['*']
                    }),
                    new PolicyStatement({
                        effect: Effect.ALLOW,
                        actions: [
                            'kms:Decrypt',
                            'kms:DecryptKey',
                            'kms:Encrypt',
                            'kms:ReEncrypt*',
                            'kms:GenerateDataKey*'
                        ],
                        resources: ['*']
                    }),
                    //new PolicyStatement({
                    //    effect: Effect.ALLOW,
                    //    actions: [
                    //        'sts:AssumeRole'
                    //    ],
                    //    resources: [Constants.CROSS_ACCOUNT_ROLE_ARN]
                    //})
                ]
            })
        })
        
        const codePipelineRole = new Role(this, 'codePipelineRole', {
            roleName: `code-pipeline-role-${Constants.PROJECT_NAME}`,
            assumedBy: new ServicePrincipal('codepipeline.amazonaws.com'),
            managedPolicies: [
                ManagedPolicy.fromAwsManagedPolicyName('AWSCodePipeline_FullAccess'),
                ManagedPolicy.fromAwsManagedPolicyName('AmazonS3FullAccess'),
                ManagedPolicy.fromAwsManagedPolicyName('AmazonEC2ContainerRegistryFullAccess'),
                codePipelineCustomPolicy
            ],
        })

        this.arn = codePipelineRole.roleArn
        this.principal = new ArnPrincipal(this.arn)
        this.role = codePipelineRole
    }
    principal: IPrincipal
    arn: string   
    role: IRole
}

