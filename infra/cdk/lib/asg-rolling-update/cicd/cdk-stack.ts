import * as cdk from 'aws-cdk-lib';
import { Stack } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { GithubSource } from '../../alb-ecs-fargate/cicd/github-source';
import { ArtifactBucket } from './artifact-bucket';
import { ArtifactKmsKey } from './artifact-kms-key';
import { AsgDeployBuild } from './asg-deploy-build';
import { CodeArtifactRepo } from './code-artifact-repo';
import { CodeBuildApp } from './code-build-app';
import { CodeBuildRole } from './code-build-role';
import { CodePipeline } from './code-pipeline';
import { CodePipelineRole } from './code-pipeline-role';

export class AsgRollingCicdStack extends Stack {
    constructor(scope: Construct, id: string, props?: cdk.StackProps) {
        super(scope, id, props);

        const kmsKey = new ArtifactKmsKey(this, 'kmsKey');
        const artifactBucket = new ArtifactBucket(this, 'artifactBucket', kmsKey);
        const codeArtifactRepo = new CodeArtifactRepo(this, 'codeArtifactRepo');
        const githubSource = new GithubSource(this, 'githubSource');
        const codeBuildRole = new CodeBuildRole(this, 'codeBuildRole', kmsKey);
        const codeBuildApp = new CodeBuildApp(this, 'codeBuildApp', artifactBucket, codeArtifactRepo, codeBuildRole);
        const asgDeployBuild = new AsgDeployBuild(this, 'asgDeployBuild', codeBuildRole);
        const pipelineRole = new CodePipelineRole(this, 'codePipelineRole');

        new CodePipeline(this, 'pipeline', artifactBucket, githubSource, codeBuildApp, asgDeployBuild, pipelineRole);
    }
}
