import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { CustomStackProps } from '../utils/custom-stack-props';
import { Network } from '../network/network';

export class AsgRollingUpdateStack extends cdk.Stack {

  constructor(scope: Construct, id: string, props?: CustomStackProps) {

    super(scope, id, props);

    const network = new Network(this, 'Network')

  }
}
