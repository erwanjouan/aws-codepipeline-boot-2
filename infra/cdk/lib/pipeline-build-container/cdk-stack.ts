import { Constants } from '../constants';
import { CodePipelineRole } from './code-pipeline-role';
import { ArtifactBucket } from './artifact-bucket';
import { CodeCommit } from './code-commit';
import { CodeBuildRole } from './code-build-role';
import { CodeBuildApp } from './code-build-app';
import { CodePipeline } from './code-pipeline';
import { CustomStackProps } from '../utils/custom-stack-props';
import { Stack } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { ImageRegistry } from './image-registry';

export class PipelineBuildContainerStack extends Stack {

  constructor(scope: Construct, id: string, props?: CustomStackProps) {

    super(scope, id, props);

    const deploymentName: string = props?.deploymentName!

    const projectDeploymentName: string = `${Constants.PROJECT_NAME}-${deploymentName}`

    const codePipelineRole = new CodePipelineRole(this, 'codePipelineRole')

    const artifactBucket = new ArtifactBucket(this, 'artifactBucket', codePipelineRole)

    const imageRegistry = new ImageRegistry(this, 'imageRegistry', deploymentName)

    const codeCommit = new CodeCommit(this, 'codeCommitRepo')

    const codeBuildRole = new CodeBuildRole(this, 'codeBuildRole')

    const codeBuildApp = new CodeBuildApp(this, 'codeBuildApp', deploymentName, projectDeploymentName, artifactBucket, imageRegistry, codeBuildRole)

    const pipeline = new CodePipeline(this, 'pipeline', deploymentName, artifactBucket, codeCommit, codeBuildApp, codePipelineRole)

  }
}
