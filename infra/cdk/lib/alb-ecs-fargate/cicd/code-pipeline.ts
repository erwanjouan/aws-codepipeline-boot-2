import { Artifact, Pipeline } from 'aws-cdk-lib/aws-codepipeline';
import { CodeBuildAction, CodeStarConnectionsSourceAction } from 'aws-cdk-lib/aws-codepipeline-actions';
import { Construct } from 'constructs';
import { ArtifactBucket } from './artifact-bucket';
import { CodeBuildApp } from './code-build-app';
import { EcsDeployBuild } from './ecs-deploy-build';
import { GithubSource } from './github-source';
import { CodePipelineRole } from './code-pipeline-role';

export class CodePipeline extends Construct {
    constructor(
        scope: Construct,
        id: string,
        artifactBucket: ArtifactBucket,
        githubSource: GithubSource,
        codeBuildApp: CodeBuildApp,
        ecsDeployBuild: EcsDeployBuild,
        pipelineRole: CodePipelineRole,
    ) {
        super(scope, id);

        const sourceArtifact = new Artifact('source');
        const buildOutput = new Artifact('build');

        const sourceAction = new CodeStarConnectionsSourceAction({
            actionName: 'GitHub',
            owner: githubSource.owner,
            repo: githubSource.repo,
            branch: githubSource.branch,
            connectionArn: githubSource.connectionArn,
            output: sourceArtifact,
        });

        const buildAction = new CodeBuildAction({
            actionName: 'BuildApp',
            project: codeBuildApp.project,
            input: sourceArtifact,
            outputs: [buildOutput],
            role: pipelineRole.role,
        });

        const deployAction = new CodeBuildAction({
            actionName: 'DeployToFargate',
            project: ecsDeployBuild.project,
            input: buildOutput,
            role: pipelineRole.role,
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
