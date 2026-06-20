import { Constants } from '../constants';
import { CodePipelineRole } from './code-pipeline-role';
import { ArtifactBucket } from './artifact-bucket';
import { CodeCommit } from './code-commit';
import { CodeBuildRole } from './code-build-role';
import { CodeBuildApp } from './code-build-app';
import { CodePipeline } from './code-pipeline';
import { Stack } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { ImageRegistry } from './image-registry';
import * as cdk from "aws-cdk-lib";

export class PipelineBuildContainerStack extends Stack {

  constructor(scope: Construct, id: string, props?: cdk.StackProps) {

    super(scope, id, props);

    const projectDeploymentName: string = `${process.env.PROJECT_NAME}-${process.env.DEPLOYMENT_NAME}`

    const codePipelineRole = new CodePipelineRole(this, 'codePipelineRole')

    const artifactBucket = new ArtifactBucket(this, 'artifactBucket', codePipelineRole)

    const imageRegistry = new ImageRegistry(this, 'imageRegistry', `${process.env.DEPLOYMENT_NAME}`)

    const codeCommit = new CodeCommit(this, 'codeCommitRepo')

    const codeBuildRole = new CodeBuildRole(this, 'codeBuildRole')

    const codeBuildApp = new CodeBuildApp(this, 'codeBuildApp', `${process.env.DEPLOYMENT_NAME}`, projectDeploymentName, artifactBucket, imageRegistry, codeBuildRole)

    const pipeline = new CodePipeline(this, 'pipeline', `${process.env.DEPLOYMENT_NAME}`, artifactBucket, codeCommit, codeBuildApp, codePipelineRole)

  }
}
