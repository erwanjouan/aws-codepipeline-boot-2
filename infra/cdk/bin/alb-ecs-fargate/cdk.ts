#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { FargateAppStack } from '../../lib/alb-ecs-fargate/app/cdk-stack';
import { FargateCicdStack } from '../../lib/alb-ecs-fargate/cicd/cdk-stack';
import { Constants } from '../../lib/constants';

const app = new cdk.App();

// Cross-account Fargate deployment:
//   fargate-app  → PROD account (ECS cluster, ALB, cross-account deploy role)
//   fargate-cicd → CICD account (CodePipeline: build + push ECR + deploy to PROD ECS)
// Deploy order: app first, then cicd.

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