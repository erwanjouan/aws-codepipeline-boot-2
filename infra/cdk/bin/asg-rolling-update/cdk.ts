#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { AsgRollingAppStack } from '../../lib/asg-rolling-update/app/cdk-stack';
import { AsgRollingCicdStack } from '../../lib/asg-rolling-update/cicd/cdk-stack';
import { Constants } from '../../lib/constants';

const app = new cdk.App();

// Cross-account ASG rolling update:
//   asg-rolling-app  → PROD account (VPC, ALB, ASG, cross-account deploy role)
//   asg-rolling-cicd → CICD account (CodePipeline: Maven build + CodeArtifact publish + instance refresh)
// Deploy order: app first, then cicd.

const asgApp = 'asg-rolling-app';
new AsgRollingAppStack(app, asgApp, {
    stackName: asgApp,
    env: {
        account: Constants.WORKLOAD_ACCOUNT_ID,
        region: Constants.DEFAULT_REGION,
    },
});

const asgCicd = 'asg-rolling-cicd';
new AsgRollingCicdStack(app, asgCicd, {
    stackName: asgCicd,
    env: {
        account: Constants.DEFAULT_ACCOUNT,
        region: Constants.DEFAULT_REGION,
    },
});
