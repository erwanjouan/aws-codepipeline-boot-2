import { Stack } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { CustomStackProps } from '../utils/custom-stack-props';
import { ArtifactKmsKey } from './artifact-kms-key';
import { ArtifactBucket } from './artifact-bucket';
import { ImageRegistry } from './image-registry';
import { CodeCommit } from './code-commit';
import { CodeBuildRole } from './code-build-role';
import { CodeBuildApp } from './code-build-app';
import { CodePipelineRole } from './code-pipeline-role';
import { CodePipeline } from './code-pipeline';

export class FargateCicdStack extends Stack {
    constructor(scope: Construct, id: string, props?: CustomStackProps) {
        super(scope, id, props);

        const kmsKey = new ArtifactKmsKey(this, 'kmsKey');
        const artifactBucket = new ArtifactBucket(this, 'artifactBucket', kmsKey);
        const imageRegistry = new ImageRegistry(this, 'imageRegistry');
        const codeCommit = new CodeCommit(this, 'codeCommit');
        const codeBuildRole = new CodeBuildRole(this, 'codeBuildRole', kmsKey);
        const codeBuildApp = new CodeBuildApp(this, 'codeBuildApp', artifactBucket, imageRegistry, codeBuildRole);
        const pipelineRole = new CodePipelineRole(this, 'codePipelineRole');

        new CodePipeline(this, 'pipeline', artifactBucket, codeCommit, codeBuildApp, pipelineRole);
    }
}