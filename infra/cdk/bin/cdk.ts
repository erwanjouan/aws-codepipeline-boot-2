#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { PipelineBuildInstanceStack } from '../lib/pipeline-build-instance/cdk-stack';
import { PipelineBuildContainerStack } from '../lib/pipeline-build-container/cdk-stack';
import { ApplicationLoadBalancedFargateServiceStack } from '../lib/application-load-balanced-fargat-service/CdkStack';
import { FargateAppStack } from '../lib/fargate-app/cdk-stack';
import { FargateCicdStack } from '../lib/fargate-cicd/cdk-stack';
import { Constants } from '../lib/constants';

const app = new cdk.App();

// All stacks are imported here
// command line will pick the one to deploy: cdk deploy $stack_name --exclusively

const buildInstance = 'pipeline-build-instance';
new PipelineBuildInstanceStack(app, buildInstance, {
  stackName: buildInstance,
  deploymentName: buildInstance,
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION
  }
})

const buildContainer = 'pipeline-build-container';
new PipelineBuildContainerStack(app, buildContainer, {
  stackName: buildContainer,
  deploymentName: buildContainer,
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION
  }
})

const fargateService = 'application-load-balanced-fargate-service'
new ApplicationLoadBalancedFargateServiceStack(app, fargateService, {
  stackName: fargateService,
  deploymentName: fargateService,
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION
  }
})

// Cross-account Fargate deployment:
//   fargate-app  → PROD account (ECS cluster, ALB, cross-account deploy role)
//   fargate-cicd → CICD account (CodePipeline: build + push ECR + deploy to PROD ECS)
// Deploy order: fargate-app first, then fargate-cicd.

const fargateApp = 'fargate-app';
new FargateAppStack(app, fargateApp, {
  stackName: fargateApp,
  deploymentName: fargateApp,
  env: {
    account: Constants.WORKLOAD_ACCOUNT_ID,
    region: Constants.DEFAULT_REGION,
  }
})

const fargateCicd = 'fargate-cicd';
new FargateCicdStack(app, fargateCicd, {
  stackName: fargateCicd,
  deploymentName: fargateCicd,
  env: {
    account: Constants.DEFAULT_ACCOUNT,
    region: Constants.DEFAULT_REGION,
  }
})