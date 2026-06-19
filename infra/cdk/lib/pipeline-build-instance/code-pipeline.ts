import { Artifact, Pipeline } from "aws-cdk-lib/aws-codepipeline";
import { Construct } from "constructs";
import { CodeBuildAction, CodeCommitSourceAction } from "aws-cdk-lib/aws-codepipeline-actions";
import { ArtifactBucket } from "./artifact-bucket";
import { CodeCommit } from "./code-commit";
import { CodeBuildApp } from "./code-build-app";
import { CodePipelineRole } from "./code-pipeline-role";

export class CodePipeline extends Construct {
    constructor(scope: Construct, id: string, deploymentName: string, artifactBucket: ArtifactBucket, codeCommit: CodeCommit,
        codeBuildApp: CodeBuildApp, codePipeline:CodePipelineRole) {
        super(scope, id);

        const sourceArtifact = new Artifact('source')

        const sourceAction = new CodeCommitSourceAction({
            actionName: "CodeCommit",
            repository: codeCommit.repo,
            output: sourceArtifact,
            branch: "cdk"
        });

        const infraArtifact = new Artifact('infra');

        const buildAppAction = new CodeBuildAction({
            actionName: "BuildApp",
            project: codeBuildApp.project,
            input: sourceArtifact,
        })

        const pipeline = new Pipeline(this, 'Pipeline', {
            pipelineName: deploymentName,
            restartExecutionOnUpdate: true,
            artifactBucket: artifactBucket.bucket,
            role: codePipeline.role,
            stages: [
                {
                    stageName: 'Source',
                    actions: [sourceAction],
                }, {
                    stageName: 'Build',
                    actions: [buildAppAction],
                }
            ]
        });

    }
}