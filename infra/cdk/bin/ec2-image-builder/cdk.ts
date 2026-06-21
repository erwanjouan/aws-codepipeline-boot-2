#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { CdkStack } from '../../lib/ec2-image-builder/CdkStack';

const app = new cdk.App();

new CdkStack(app, process.env.DEPLOYMENT_NAME!, {
    stackName: process.env.DEPLOYMENT_NAME!,
    env: {
        account: process.env.CICD_ACCOUNT_ID,
        region: process.env.CDK_DEFAULT_REGION,
  }
});