import * as cdk from 'aws-cdk-lib';
import { Stack } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { Alb } from './alb';
import { Asg } from './asg';
import { CrossAccountDeployRole } from './cross-account-deploy-role';
import { Ec2Role } from './ec2-role';
import { Vpc } from './vpc';
import { WebappSg } from './webapp-sg';

export class AsgRollingAppStack extends Stack {
    constructor(scope: Construct, id: string, props?: cdk.StackProps) {
        super(scope, id, props);

        const vpc = new Vpc(this, 'vpc');
        const alb = new Alb(this, 'alb', vpc);
        const ec2Role = new Ec2Role(this, 'ec2Role');
        const webappSg = new WebappSg(this, 'webappSg', vpc.vpc, alb.securityGroup);
        new Asg(this, 'asg', vpc, alb.targetGroup, ec2Role.role, webappSg.securityGroup);
        new CrossAccountDeployRole(this, 'crossAccountDeployRole');
    }
}
