import { Artifact, Pipeline } from 'aws-cdk-lib/aws-codepipeline';
import { CodeBuildAction, CodeCommitSourceAction, EcsDeployAction } from 'aws-cdk-lib/aws-codepipeline-actions';
import { Role } from 'aws-cdk-lib/aws-iam';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import { Construct } from 'constructs';
import { Constants } from '../constants';
import { ArtifactBucket } from './artifact-bucket';
import { CodeBuildApp } from './code-build-app';
import { CodeCommit } from './code-commit';
import { CodePipelineRole } from './code-pipeline-role';

export class CodePipeline extends Construct {
    constructor(
        scope: Construct,
        id: string,
        artifactBucket: ArtifactBucket,
        codeCommit: CodeCommit,
        codeBuildApp: CodeBuildApp,
        pipelineRole: CodePipelineRole,
    ) {
        super(scope, id);

        const sourceArtifact = new Artifact('source');
        const buildOutput = new Artifact('build');

        const sourceAction = new CodeCommitSourceAction({
            actionName: 'CodeCommit',
            repository: codeCommit.repo,
            output: sourceArtifact,
            branch: 'cdk',
        });

        const buildAction = new CodeBuildAction({
            actionName: 'BuildApp',
            project: codeBuildApp.project,
            input: sourceArtifact,
            outputs: [buildOutput],
        });

        // Cross-account role in PROD that CodePipeline will assume to call ECS
        const crossAccountRole = Role.fromRoleArn(
            this,
            'CrossAccountRole',
            `arn:aws:iam::${Constants.WORKLOAD_ACCOUNT_ID}:role/${Constants.FARGATE_CROSS_ACCOUNT_ROLE_NAME}`,
        );

        // Minimal IBaseService representation of the cross-account ECS service.
        // EcsDeployAction only needs cluster.clusterName, serviceName, and env.account
        // at synthesis time to generate the CloudFormation action configuration.
        const ecsService = {
            env: { account: Constants.WORKLOAD_ACCOUNT_ID, region: Constants.DEFAULT_REGION },
            serviceName: Constants.FARGATE_SERVICE_NAME,
            cluster: {
                clusterName: Constants.FARGATE_CLUSTER_NAME,
            },
        } as unknown as ecs.IBaseService;

        const deployAction = new EcsDeployAction({
            actionName: 'DeployToFargate',
            service: ecsService,
            input: buildOutput,
            role: crossAccountRole,
        });

        new Pipeline(this, 'Pipeline', {
            pipelineName: 'fargate',
            artifactBucket: artifactBucket.bucket,
            role: pipelineRole.role,
            stages: [
                { stageName: 'Source', actions: [sourceAction] },
                { stageName: 'Build', actions: [buildAction] },
                { stageName: 'Deploy', actions: [deployAction] },
            ],
        });
    }
}