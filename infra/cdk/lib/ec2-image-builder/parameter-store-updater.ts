import cdk = require('aws-cdk-lib');
import customResources = require('aws-cdk-lib/custom-resources');
import lambda = require('aws-cdk-lib/aws-lambda');
import { Construct } from 'constructs';

import fs = require('fs');
import { Effect, ManagedPolicy, PolicyStatement, Role, ServicePrincipal } from 'aws-cdk-lib/aws-iam';
import { Constants } from '../constants';
import { Ec2Architecture } from '../utils/ec2-architecture';
const path = require('path');

export class ParameterStoreUpdater extends Construct {
  public readonly response: string;

  constructor(scope: Construct, id: string, amiId:string, architecture:Ec2Architecture) {
    super(scope, id);

    const parameterStoreArn = `arn:aws:ssm:${Constants.DEFAULT_REGION}:${Constants.DEFAULT_ACCOUNT}:parameter${Constants.PARAMETER_STORE_AMI}/${architecture.label}`
    
    const allowSsm:ManagedPolicy = new ManagedPolicy(this, 'allowSsmPoliciy', {
      statements: [
        new PolicyStatement({
            effect: Effect.ALLOW,
            actions: [
                "ssm:PutParameter",
                "ssm:DeleteParameter",
                "ssm:GetParameterHistory",
                "ssm:GetParameter"
            ],
            resources: [parameterStoreArn]
        })]
    })

    
    const role:Role = new Role(this, 'customResourceRole', {
      assumedBy: new ServicePrincipal('lambda.amazonaws.com'),
      managedPolicies: [
        allowSsm,
        ManagedPolicy.fromAwsManagedPolicyName('service-role/AWSLambdaBasicExecutionRole')
      ]
    })

    const fn = new lambda.SingletonFunction(this, 'Singleton', {
      uuid: 'f7d4f730-4ee1-11e8-9c2d-fa7ae01bbebd',
      code: new lambda.InlineCode(fs.readFileSync(path.join('lib', 'ec2-image-builder','lambda','custom-resource-handler.py'), { encoding: 'utf-8' })),
      handler: 'index.main',
      timeout: cdk.Duration.seconds(300),
      runtime: lambda.Runtime.PYTHON_3_9,
      role: role
    });

    const provider = new customResources.Provider(this, 'Provider', {
      onEventHandler: fn,
    });

    const resource = new cdk.CustomResource(this, 'Resource', {
      serviceToken: provider.serviceToken,
      properties: {
        'AmiId': amiId,
        'ParameterStoreName': `${Constants.PARAMETER_STORE_AMI}/${architecture.label}`
      },
    });

    this.response = resource.getAttString('Response');
  }
}
