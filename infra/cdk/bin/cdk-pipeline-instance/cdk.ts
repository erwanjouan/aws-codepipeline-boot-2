#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { CdkPipelineInstanceStack } from '../../lib/cdk-pipeline-instance/CdkStack';

const app = new cdk.App();

const deploymentName = app.node.tryGetContext("deploymentName");
const stackName = `${deploymentName}-cicd`;

new CdkPipelineInstanceStack(app, stackName, {
  stackName: stackName,
  deploymentName: deploymentName
});