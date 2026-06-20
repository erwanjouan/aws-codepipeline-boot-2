#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { CdkStack } from '../../lib/ec2-image-builder/CdkStack';
import {Constants} from "../../lib/constants";

const app = new cdk.App();

new CdkStack(app, process.env.DEPLOYMENT_NAME!, {
    stackName: process.env.DEPLOYMENT_NAME!,
    env: {
        account: Constants.DEFAULT_ACCOUNT,
        region: Constants.DEFAULT_REGION,
  }
});