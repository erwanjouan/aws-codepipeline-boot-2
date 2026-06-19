import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { CustomStackProps } from '../utils/custom-stack-props';
import { CodePipeline, CodePipelineSource, ShellStep } from 'aws-cdk-lib/pipelines';
import { Repository } from 'aws-cdk-lib/aws-codecommit';
import { AppStage } from './app-stage';
import { Constants } from '../constants';
import { StageProps } from 'aws-cdk-lib';

export class CdkPipelineInstanceStack extends cdk.Stack {

  constructor(scope: Construct, id: string, props?: CustomStackProps) {

    super(scope, id, props);
    
    const repository = Repository.fromRepositoryName(this, 'CodeCommitRepo', 'aws-codepipeline-boot');
    const deploymentName = props?.deploymentName!
    
    const pipeline = new CodePipeline(this, "CDKPipeline", {
      crossAccountKeys: true,
      pipelineName: "CDKPipeline",
      synth: new ShellStep("deploy", {
        input: CodePipelineSource.codeCommit(repository, 'cdk'),
        commands: [ 
          "cd ./infra/cdk/",
          "npm ci",
          `npx cdk synth -c deploymentName=${deploymentName} ${deploymentName}-app`
        ],
        primaryOutputDirectory: "./infra/cdk/cdk.out"
      }),
    });

    const stageProps:StageProps = {
      env: { 
        account: Constants.WORKLOAD_ACCOUNT_ID, 
        region: Constants.DEFAULT_REGION
      }
    }

    pipeline.addStage(new AppStage(this, "prod", deploymentName, stageProps))
    
  }
}
